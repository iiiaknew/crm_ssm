package com.iaknew.crm.workbench.web.controller;

import com.iaknew.crm.settings.domain.User;
import com.iaknew.crm.settings.service.UserService;
import com.iaknew.crm.utils.*;
import com.iaknew.crm.vo.PaginationVo;
import com.iaknew.crm.workbench.domain.Activity;
import com.iaknew.crm.workbench.domain.ActivityRemark;
import com.iaknew.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/activity")
public class ActivityController {
    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;

    @RequestMapping("/getUserList.do")
    @ResponseBody
    public List<User> getUserList(){
        return userService.getUserList();
    }

    @RequestMapping("/save.do")
    @ResponseBody
    public boolean saveActivity(Activity activity, HttpServletRequest request){
        System.out.println("进入创建市场活动操作");
        activity.setId(UUIDUtil.getUUID());
        activity.setCreateTime(DateTimeUtil.getSysTime());
        User user = (User) request.getSession().getAttribute("user");
        activity.setCreateBy(user.getName());

        boolean flag = activityService.saveActivity(activity);
        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);

        return flag;
    }

    @RequestMapping("/pageList.do")
    @ResponseBody
    public PaginationVo<Activity> pageList(String name, String owner, String startDate, String endDate,
                                           @RequestParam("pageNo") String pageNoStr, @RequestParam("pageSize") String pageSizeStr){
        System.out.println("进入查询市场活动操作");
        int pageNo = Integer.valueOf(pageNoStr);
        // 每页条数
        int pageSize = Integer.valueOf(pageSizeStr);
        // 略过条数
        int skipCount = (pageNo-1) * pageSize;

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("skipCount", skipCount);
        map.put("pageSize", pageSize);

        PaginationVo<Activity> vo = activityService.pageList(map);

        return vo;
    }

    @RequestMapping("/delete.do")
    @ResponseBody
    public boolean delete(HttpServletRequest request){
        System.out.println("进入删除市场活动操作");

        String[] ids = request.getParameterValues("id");
        boolean flag = activityService.delete(ids);

        return flag;
    }

    @RequestMapping("/getUserListAndActivity.do")
    @ResponseBody
    public Map<String, Object> getUserListAndActivity(String id){
         Map<String, Object> map = activityService.getUserListAndActivity(id);
         return map;
    }

    @RequestMapping("/update.do")
    @ResponseBody
    public boolean update(Activity activity, HttpServletRequest request){
        activity.setEditTime(DateTimeUtil.getSysTime());
        User user = (User) request.getSession().getAttribute("user");
        activity.setEditBy(user.getName());

        boolean flag = activityService.update(activity);
        return flag;
    }

    @RequestMapping("/detail.do")
    public void detail(String id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Activity a = activityService.detail(id);
        request.setAttribute("a", a);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request, response);
    }

    @RequestMapping("/getRemarkByAid.do")
    @ResponseBody
    public List<ActivityRemark> getRemarkByAid(String activityId){
        List<ActivityRemark> remarks = activityService.getRemarkByAid(activityId);
        return remarks;
    }

    @RequestMapping("/deleteRemark.do")
    @ResponseBody
    public boolean deleteRemark(String id){
        boolean flag = activityService.deleteRemark(id);
        return flag;
    }

    @RequestMapping("/saveRemark.do")
    @ResponseBody
    public boolean saveRemark(ActivityRemark ar, HttpServletRequest request){
        ar.setId(UUIDUtil.getUUID());
        ar.setCreateTime(DateTimeUtil.getSysTime());
        ar.setCreateBy(((User)(request.getSession().getAttribute("user"))).getName());
        ar.setEditFlag("0");
        boolean flag = activityService.saveRemark(ar);
        return flag;
    }

    @RequestMapping("/updateRemark.do")
    @ResponseBody
    public boolean updateRemark(ActivityRemark ar, HttpServletRequest request){
        ar.setEditTime(DateTimeUtil.getSysTime());
        ar.setEditBy(((User)(request.getSession().getAttribute("user"))).getName());
        ar.setEditFlag("1");
        boolean flag = activityService.updateRemark(ar);
        return flag;
    }
}
