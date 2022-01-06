package com.iaknew.crm.settings.service.impl;

import com.iaknew.crm.settings.dao.DicTypeDao;
import com.iaknew.crm.settings.dao.DicValueDao;
import com.iaknew.crm.settings.domain.DicType;
import com.iaknew.crm.settings.domain.DicValue;
import com.iaknew.crm.settings.service.DicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DicServiceImpl implements DicService {
    @Autowired
    private DicTypeDao dicTypeDao;
    @Autowired
    private DicValueDao dicValueDao;

    @Override
    public Map<String, List<DicValue>> getAll() {
        Map<String, List<DicValue>> map = new HashMap<>();
        //查询typeCode
        List<DicType> dtList = dicTypeDao.getTypeList();
        //查询value
        for(DicType dt : dtList){
            String code = dt.getCode();
            List<DicValue> dvList = dicValueDao.getListByCode(code);
            map.put(code+"List", dvList);
        }
        return map;
    }
}
