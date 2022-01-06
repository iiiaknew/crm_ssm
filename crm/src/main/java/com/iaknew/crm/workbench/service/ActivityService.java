package com.iaknew.crm.workbench.service;

import com.iaknew.crm.settings.domain.User;
import com.iaknew.crm.vo.PaginationVo;
import com.iaknew.crm.workbench.domain.Activity;
import com.iaknew.crm.workbench.domain.ActivityRemark;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface ActivityService {

    boolean saveActivity(Activity activity);

    PaginationVo<Activity> pageList(Map<String, Object> map);

    boolean delete(String[] ids);

    Map<String, Object> getUserListAndActivity(String id);

    boolean update(Activity activity);

    Activity detail(String id);

    List<ActivityRemark> getRemarkByAid(String activityId);

    boolean deleteRemark(String id);

    boolean saveRemark(ActivityRemark ar);

    boolean updateRemark(ActivityRemark ar);

    List<Activity> getActivityListByClueId(String clueId);

    List<Activity> getActivityListByNameAndNotBound(Map<String, String> map);

    List<Activity> getActivityListByName(String aname);
}
