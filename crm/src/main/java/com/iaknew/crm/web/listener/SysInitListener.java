package com.iaknew.crm.web.listener;

import com.iaknew.crm.settings.domain.DicType;
import com.iaknew.crm.settings.domain.DicValue;
import com.iaknew.crm.settings.service.DicService;
import com.iaknew.crm.settings.service.impl.DicServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.*;

@WebListener
public class SysInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {

        //处理数据字典
        DicService dicService = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()).getBean(DicServiceImpl.class);
        ServletContext application = event.getServletContext();
        //查询typeCode
        Map<String, List<DicValue>> map = dicService.getAll();

        Set<String> set = map.keySet();
        for(String key : set){
            List<DicValue> value = map.get(key);
            application.setAttribute(key, value);
        }

        Map<String, String> pMap = new HashMap<>();
        //解析Stage2Possibility.properties文件
        ResourceBundle rb = ResourceBundle.getBundle("conf/Stage2Possibility");
        Enumeration<String> e = rb.getKeys();
        while(e.hasMoreElements()){
            String key = e.nextElement();
            String value = rb.getString(key);
            pMap.put(key, value);
        }

        application.setAttribute("pMap", pMap);

    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }
}
