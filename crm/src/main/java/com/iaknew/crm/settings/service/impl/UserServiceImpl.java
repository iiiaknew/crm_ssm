package com.iaknew.crm.settings.service.impl;

import com.iaknew.crm.settings.dao.UserDao;
import com.iaknew.crm.settings.domain.User;
import com.iaknew.crm.settings.exception.LoginException;
import com.iaknew.crm.settings.service.UserService;
import com.iaknew.crm.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);

        User user = userDao.login(map);
        if(user == null){
            throw new LoginException("账号密码不正确");
        }

        // 验证失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if(expireTime.compareTo(currentTime) < 0){
            throw new LoginException("账号已失效");
        }

        // 验证锁定状态
        String lockState = user.getLockState();
        if("0".equals(lockState)){
            throw new LoginException("账号已锁定");
        }

        // 验证ip
        String allowIps = user.getAllowIps();
        if(!allowIps.contains(ip)){
            throw new LoginException("该ip没有权限访问");
        }

        return user;
    }

    @Override
    public List<User> getUserList() {
        return userDao.selectUsers();
    }
}
