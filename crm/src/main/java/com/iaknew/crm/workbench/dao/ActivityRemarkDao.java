package com.iaknew.crm.workbench.dao;

import com.iaknew.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {
    int getCountByAid(String[] ids);

    int deleteByAid(String[] ids);

    List<ActivityRemark> getRemarkByAid(String activityId);

    int deleteById(String id);

    int saveRemark(ActivityRemark ar);

    int update(ActivityRemark ar);
}
