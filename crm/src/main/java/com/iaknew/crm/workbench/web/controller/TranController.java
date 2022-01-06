package com.iaknew.crm.workbench.web.controller;

import com.iaknew.crm.settings.domain.User;
import com.iaknew.crm.settings.service.UserService;
import com.iaknew.crm.utils.DateTimeUtil;
import com.iaknew.crm.utils.UUIDUtil;
import com.iaknew.crm.workbench.domain.Tran;
import com.iaknew.crm.workbench.domain.TranHistory;
import com.iaknew.crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/tran")
public class TranController {

    @Autowired
    private UserService userService;
    @Autowired
    private TranService tranService;

    @RequestMapping("/add.do")
    public ModelAndView add(){
        ModelAndView mv = new ModelAndView();
        List<User> uList = userService.getUserList();

        mv.addObject("uList", uList);
        mv.setViewName("transaction/save");
        return mv;
    }

    @RequestMapping("/getCustomerName.do")
    @ResponseBody
    public List<String> getCustomerName(String name){
        System.out.println("根据客户名称 模糊查询");
        List<String> sList = tranService.getCustomerName(name);
        return sList;
    }

    @RequestMapping("/save.do")
    public ModelAndView save(String customerName, Tran tran, HttpServletRequest request){
        ModelAndView mv = new ModelAndView();

        tran.setId(UUIDUtil.getUUID());
        tran.setCreateTime(DateTimeUtil.getSysTime());
        tran.setCreateBy(((User)request.getSession().getAttribute("user")).getName());
        boolean flag = tranService.save(customerName, tran);
        if(flag){
            mv.setViewName("redirect:/workbench/transaction/index.jsp");
        }

        return mv;
    }

    @RequestMapping("detail.do")
    public ModelAndView detail(String id, HttpServletRequest request){
        ModelAndView mv = new ModelAndView();

        Tran t = tranService.detail(id);
        String stage = t.getStage();

        //取阶段对应的可能性
        ServletContext application = request.getServletContext();
        Map<String, String> map = (Map<String, String>) application.getAttribute("pMap");

        String possibility = map.get(stage);

        mv.addObject("t", t);
        mv.addObject("possibility", possibility);
        mv.setViewName("forward:/workbench/transaction/detail.jsp");

        return mv;
    }

    @RequestMapping("/getTranHistoryList.do")
    @ResponseBody
    public List<TranHistory> getTranHistoryList(String tranId, HttpServletRequest request){
        System.out.println("获取交易历史列表");

        ServletContext application = request.getServletContext();
        Map<String, String> map = (Map<String, String>) application.getAttribute("pMap");

        List<TranHistory> thList = tranService.getTranHistoryList(tranId);

        for(TranHistory th : thList){
            String stage = th.getStage();
            String possibility = map.get(stage);
            th.setPossibility(possibility);
        }

        return thList;
    }

    @RequestMapping("/changeStage.do")
    @ResponseBody
    public Map<String, Object> changeStage(Tran t, HttpServletRequest request){

        Map<String, Object> map = new HashMap<>();

        ServletContext application = request.getServletContext();
        Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");
        String possibility = pMap.get(t.getStage());

        t.setEditTime(DateTimeUtil.getSysTime());
        t.setEditBy(((User)request.getSession().getAttribute("user")).getName());
        boolean flag = tranService.changeStage(t);

        map.put("success", flag);
        map.put("t", t);
        map.put("possibility", possibility);


        return map;
    }
}
