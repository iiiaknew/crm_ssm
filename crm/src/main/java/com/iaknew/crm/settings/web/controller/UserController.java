package com.iaknew.crm.settings.web.controller;

import com.iaknew.crm.settings.domain.User;
import com.iaknew.crm.settings.service.UserService;
import com.iaknew.crm.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/login.do")
    @ResponseBody
    public Map<String, Object> login(String loginAct, String loginPwd, HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();

        loginPwd = MD5Util.getMD5(loginPwd);
        String ip = request.getRemoteAddr();
        System.out.println(ip);
        try {
            User user = userService.login(loginAct, loginPwd, ip);
            request.getSession().setAttribute("user", user);
            map.put("success", true);
        }catch (Exception e){
            e.printStackTrace();
            String msg = e.getMessage();
            map.put("success", false);
            map.put("msg", msg);
        }

        return map;
    }
}
