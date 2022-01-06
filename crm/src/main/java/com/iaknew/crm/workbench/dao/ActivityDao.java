package com.iaknew.crm.workbench.dao;

import com.iaknew.crm.settings.domain.User;
import com.iaknew.crm.workbench.domain.Activity;
import com.iaknew.crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityDao {
    int saveActivity(Activity activity);

    List<Activity> getActivityListByCondition(Map<String, Object> map);

    int getTotalByCondition(Map<String, Object> map);

    int delete(String[] ids);

    Activity getActivityById(String id);

    int update(Activity activity);

    Activity detail(String id);

    List<Activity> getActivityListByClueId(String clueId);

    List<Activity> getActivityListByNameAndNotBound(Map<String, String> map);

    List<Activity> getActivityListByName(String aname);
}
