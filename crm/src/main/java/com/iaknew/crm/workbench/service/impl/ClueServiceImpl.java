package com.iaknew.crm.workbench.service.impl;

import com.iaknew.crm.utils.DateTimeUtil;
import com.iaknew.crm.utils.UUIDUtil;
import com.iaknew.crm.workbench.dao.*;
import com.iaknew.crm.workbench.domain.*;
import com.iaknew.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClueServiceImpl implements ClueService {
    @Autowired
    private ClueDao clueDao;
    @Autowired
    private ClueActivityRelationDao clueActivityRelationDao;
    @Autowired
    private ClueRemarkDao clueRemarkDao;
    //    交易相关表
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private CustomerRemarkDao customerRemarkDao;
    @Autowired
    private ContactsDao contactsDao;
    @Autowired
    private ContactsRemarkDao contactsRemarkDao;
    @Autowired
    private ContactsActivityRelationDao contactsActivityRelationDao;
    //    转换相关表
    @Autowired
    private TranDao tranDao;
    @Autowired
    private TranHistoryDao tranHistoryDao;

    @Override
    public boolean save(Clue clue) {
        boolean flag = true;
        int count = clueDao.save(clue);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Clue getClueById(String id) {
        Clue clue = clueDao.getClueById(id);
        return clue;
    }

    @Override
    public boolean unbound(String id) {
        boolean flag = true;
        int count = clueActivityRelationDao.unbound(id);
        if(count != 1){
            flag = false;
        };
        return flag;
    }

    @Override
    public boolean bound(String clueId, String[] aids) {
        boolean flag = true;

        for(String aid:aids){
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setClueId(clueId);
            car.setActivityId(aid);

            int count = clueActivityRelationDao.bound(car);
            if(count != 1){
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran tran, String createBy) {
        boolean flag = true;

        //(1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue clue = clueDao.getById(clueId);

        //(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        String company = clue.getCompany();
        Customer cus = customerDao.getCustomerByName(company);
        if(cus == null){
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setWebsite(clue.getWebsite());
            cus.setPhone(clue.getPhone());
            cus.setOwner(clue.getOwner());
            cus.setNextContactTime(clue.getNextContactTime());
            cus.setName(clue.getCompany());
            cus.setDescription(clue.getDescription());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setCreateBy(createBy);
            cus.setContactSummary(clue.getContactSummary());
            cus.setAddress(clue.getAddress());

            int count = customerDao.save(cus);
            if(count != 1){
                flag = false;
            }
        }

        //(3) 通过线索对象提取联系人信息，保存联系人
        Contacts con = new Contacts();
        con.setSource(clue.getSource());
        con.setOwner(clue.getOwner());
        con.setNextContactTime(clue.getNextContactTime());
        con.setMphone(clue.getMphone());
        con.setJob(clue.getJob());
        con.setId(UUIDUtil.getUUID());
        con.setFullname(clue.getFullname());
        con.setEmail(clue.getEmail());
        con.setDescription(clue.getDescription());
        con.setCustomerId(cus.getId());
        con.setCreateTime(DateTimeUtil.getSysTime());
        con.setCreateBy(createBy);
        con.setContactSummary(clue.getContactSummary());
        con.setAppellation(clue.getAppellation());
        con.setAddress(clue.getAddress());
        int count1 = contactsDao.save(con);
        if(count1 != 1){
            flag = false;
        }

        //(4) 线索备注转换到客户备注以及联系人备注
        List<ClueRemark> clueRemarkList = clueRemarkDao.getByClueId(clueId);
        for(ClueRemark clueRemark : clueRemarkList){
            String noteContent = clueRemark.getNoteContent();

            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCustomerId(cus.getId());
            customerRemark.setNoteContent(noteContent);
            customerRemark.setCreateTime(DateTimeUtil.getSysTime());
            customerRemark.setCreateBy(createBy);
            customerRemark.setEditFlag("0");
            int count2 = customerRemarkDao.save(customerRemark);
            if(count2 != 1){
                flag = false;
            }

            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setContactsId(con.getId());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(DateTimeUtil.getSysTime());
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setEditFlag("0");
            int count3 = contactsRemarkDao.save(contactsRemark);
            if(count3 != 1){
                flag = false;
            }
        }

        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        for(ClueActivityRelation clueActivityRelation : clueActivityRelationList){
            String activityId = clueActivityRelation.getActivityId();

            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(con.getId());
            int count4 = contactsActivityRelationDao.save(contactsActivityRelation);
            if(count4 != 1){
                flag = false;
            }
        }

        //(6) 如果有创建交易需求，创建一条交易
        if(tran != null){
            tran.setContactsId(con.getId());
            tran.setContactSummary(clue.getContactSummary());
            tran.setSource(clue.getSource());
            tran.setOwner(clue.getOwner());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setDescription(clue.getDescription());
            tran.setCustomerId(cus.getId());
            int count5 = tranDao.save(tran);
            if(count5 != 1){
                flag = false;
            }

            //(7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setTranId(tran.getId());
            tranHistory.setStage(tran.getStage());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateTime(DateTimeUtil.getSysTime());
            tranHistory.setCreateBy(createBy);
            int count6 = tranHistoryDao.save(tranHistory);
            if(count6 != 1){
                flag = false;
            }
        }

        //(8) 删除线索备注
        for(ClueRemark clueRemark : clueRemarkList){
            int count7 = clueRemarkDao.delete(clueRemark);
            if(count7 != 1){
                flag = false;
            }
        }
        //(9) 删除线索和市场活动的关系
        for(ClueActivityRelation clueActivityRelation : clueActivityRelationList){
            int count8 = clueActivityRelationDao.delete(clueActivityRelation);
            if(count8 != 1){
                flag = false;
            }
        }
        //(10) 删除线索
        int count9 = clueDao.deleteById(clueId);
        if(count9 != 1){
            flag = false;
        }
        return flag;
    }
}
