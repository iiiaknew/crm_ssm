package com.iaknew.crm.workbench.web.controller;

import com.iaknew.crm.settings.domain.User;
import com.iaknew.crm.settings.service.UserService;
import com.iaknew.crm.utils.DateTimeUtil;
import com.iaknew.crm.utils.UUIDUtil;
import com.iaknew.crm.workbench.domain.Activity;
import com.iaknew.crm.workbench.domain.Clue;
import com.iaknew.crm.workbench.domain.Tran;
import com.iaknew.crm.workbench.service.ActivityService;
import com.iaknew.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/clue")
public class ClueController {

    @Autowired
    private UserService userService;
    @Autowired
    private ClueService clueService;
    @Autowired
    private ActivityService activityService;

    @RequestMapping("/getUserList.do")
    @ResponseBody
    public List<User> getUserList(){
        List<User> users = userService.getUserList();
        return users;
    }

    @RequestMapping("/save.do")
    @ResponseBody
    public boolean save(Clue clue, HttpServletRequest request){
        clue.setId(UUIDUtil.getUUID());
        clue.setCreateTime(DateTimeUtil.getSysTime());
        clue.setCreateBy(((User)(request.getSession().getAttribute("user"))).getName());
        boolean flag = clueService.save(clue);
        return flag;
    }

    @RequestMapping("/detail.do")
    @ResponseBody
    public void detail(String id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Clue c = clueService.getClueById(id);
        request.setAttribute("c", c);

        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request, response);
    }

    @RequestMapping("/getActivityListByClueId.do")
    @ResponseBody
    public List<Activity> getActivityListByClueId(String clueId) {

        return activityService.getActivityListByClueId(clueId);
    }

    @RequestMapping("/unbound.do")
    @ResponseBody
    public boolean unbound(String id) {

        return clueService.unbound(id);
    }

    @RequestMapping("/getActivityListByNameAndNotBound.do")
    @ResponseBody
    public List<Activity> getActivityListByNameAndNotBound(String clueId, String aname) {
        Map<String, String> map = new HashMap<>();
        map.put("clueId", clueId);
        map.put("aname", aname);
        return activityService.getActivityListByNameAndNotBound(map);
    }

    @RequestMapping("/bound.do")
    @ResponseBody
    public boolean bound(HttpServletRequest request, HttpServletResponse response) {
        String clueId = request.getParameter("cid");
        String[] aids = request.getParameterValues("aid");

        return clueService.bound(clueId, aids);
    }

    @RequestMapping("/getActivityListByName.do")
    @ResponseBody
    public List<Activity> getActivityListByName(String aname) {

        return activityService.getActivityListByName(aname);
    }

    @RequestMapping("/convert.do")
    public ModelAndView convert(String clueId, String flag, HttpServletRequest request){
        System.out.println("转换操作");
        ModelAndView mv = new ModelAndView();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        Tran tran = null;
        if("a".equals(flag)){
            tran = new Tran();

            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");

            tran.setId(UUIDUtil.getUUID());
            tran.setCreateTime(DateTimeUtil.getSysTime());
            tran.setCreateBy(createBy);
            tran.setMoney(money);
            tran.setName(name);
            tran.setStage(stage);
            tran.setExpectedDate(expectedDate);
            tran.setActivityId(activityId);
        }

        boolean flag1 = clueService.convert(clueId, tran, createBy);

        if(flag1){
            mv.setViewName("redirect:/workbench/clue/index.jsp");
        }

        mv.addObject(flag1);

        return mv;
    }
}
