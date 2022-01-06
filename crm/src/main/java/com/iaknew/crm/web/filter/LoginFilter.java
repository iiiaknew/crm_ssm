package com.iaknew.crm.web.filter;

import com.iaknew.crm.settings.domain.User;
import org.springframework.context.annotation.ComponentScan;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "loginFilter", urlPatterns = {"*.do", "*.jsp"})
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession();
        String path = request.getServletPath();
        if("/login.jsp".equals(path) || "/user/login.do".equals(path)){
            chain.doFilter(req, resp);
        }else{
            User user = (User) session.getAttribute("user");

            if(user != null){
                chain.doFilter(req,resp);
            }else{
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
        }
    }

    @Override
    public void destroy() {

    }
}
