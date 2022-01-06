package com.iaknew.crm.workbench.service.impl;

import com.iaknew.crm.settings.dao.UserDao;
import com.iaknew.crm.settings.domain.User;
import com.iaknew.crm.vo.PaginationVo;
import com.iaknew.crm.workbench.dao.ActivityDao;
import com.iaknew.crm.workbench.dao.ActivityRemarkDao;
import com.iaknew.crm.workbench.domain.Activity;
import com.iaknew.crm.workbench.domain.ActivityRemark;
import com.iaknew.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ActivityRemarkDao activityRemarkDao;
    @Autowired
    private UserDao userDao;

    @Override
    public boolean saveActivity(Activity activity) {
        int nums = activityDao.saveActivity(activity);
        boolean flag = false;

        if(nums == 1){
            flag = true;
        }
        return flag;
    }

    @Override
    public PaginationVo<Activity> pageList(Map<String, Object> map) {
        // 查询total
        int total = activityDao.getTotalByCondition(map);
        // 查询dataList
        List<Activity> dataList = activityDao.getActivityListByCondition(map);

        PaginationVo<Activity> vo = new PaginationVo<>();
        vo.setDataList(dataList);
        vo.setTotal(total);

        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;
        // 查需要删除市场活动备注的条数
        int count1 = activityRemarkDao.getCountByAid(ids);
        // 删除市场活动备注
        int count2 = activityRemarkDao.deleteByAid(ids);

        if(count1 != count2){
            flag = false;
        }
        // 删除市场活动
        int count3 = activityDao.delete(ids);
        if(count3 != ids.length){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        // get userList
        List<User> uList = userDao.selectUsers();

        // get activity
        Activity a = activityDao.getActivityById(id);

        // 封装map
        Map<String, Object> map = new HashMap<>();
        map.put("uList", uList);
        map.put("a", a);

        return map;
    }

    @Override
    public boolean update(Activity activity) {
        boolean flag = true;

        int count = activityDao.update(activity);
        if(count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Activity detail(String id) {
        Activity a = activityDao.detail(id);
        return a;
    }

    @Override
    public List<ActivityRemark> getRemarkByAid(String activityId) {
        List<ActivityRemark> remarks = activityRemarkDao.getRemarkByAid(activityId);
        return remarks;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag = true;
        int count = activityRemarkDao.deleteById(id);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.saveRemark(ar);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.update(ar);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        return activityDao.getActivityListByClueId(clueId);
    }

    @Override
    public List<Activity> getActivityListByNameAndNotBound(Map<String, String> map) {
        return activityDao.getActivityListByNameAndNotBound(map);
    }

    @Override
    public List<Activity> getActivityListByName(String aname) {
        return activityDao.getActivityListByName(aname);
    }

}
