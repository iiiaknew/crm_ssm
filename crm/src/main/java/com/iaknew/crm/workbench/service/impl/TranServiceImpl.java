package com.iaknew.crm.workbench.service.impl;

import com.iaknew.crm.utils.DateTimeUtil;
import com.iaknew.crm.utils.UUIDUtil;
import com.iaknew.crm.workbench.dao.CustomerDao;
import com.iaknew.crm.workbench.dao.TranDao;
import com.iaknew.crm.workbench.dao.TranHistoryDao;
import com.iaknew.crm.workbench.domain.Customer;
import com.iaknew.crm.workbench.domain.Tran;
import com.iaknew.crm.workbench.domain.TranHistory;
import com.iaknew.crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TranServiceImpl implements TranService {

    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private TranDao tranDao;
    @Autowired
    private TranHistoryDao tranHistoryDao;

    @Override
    public List<String> getCustomerName(String name) {
        List<String> sList = customerDao.getCustomerName(name);
        return sList;
    }

    @Override
    public boolean save(String customerName, Tran tran) {

        boolean flag = true;

        Customer cus = customerDao.getCustomerByName(customerName);
        if(cus == null){
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setOwner(tran.getOwner());
            cus.setNextContactTime(tran.getNextContactTime());
            cus.setDescription(tran.getDescription());
            cus.setContactSummary(tran.getContactSummary());
            cus.setCreateBy(tran.getCreateBy());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setName(customerName);

            int count = customerDao.save(cus);
            if(count != 1){
                flag = false;
            }

        }
        tran.setCustomerId(cus.getId());

        int count1 = tranDao.save(tran);
        if(count1 != 1){
            flag = false;
        }

        //添加交易历史
        TranHistory th = new TranHistory();
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setTranId(tran.getId());
        th.setCreateBy(tran.getCreateBy());
        th.setId(UUIDUtil.getUUID());
        th.setStage(tran.getStage());
        th.setMoney(tran.getMoney());
        th.setExpectedDate(tran.getExpectedDate());

        int count2 = tranHistoryDao.save(th);
        if(count2 != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Tran detail(String id) {
        return tranDao.detail(id);
    }

    @Override
    public List<TranHistory> getTranHistoryList(String tranId) {
        List<TranHistory> tList = tranHistoryDao.getTranHistoryList(tranId);
        return tList;
    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag = true;

        int count1 = tranDao.changeStage(t);
        if(count1 != 1){
            flag = false;
        }

        TranHistory th = new TranHistory();
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setTranId(t.getId());
        th.setCreateBy(t.getEditBy());
        th.setId(UUIDUtil.getUUID());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setStage(t.getStage());
        int count2 = tranHistoryDao.save(th);
        if(count2 != 1){
            flag = false;
        }

        return flag;
    }
}
