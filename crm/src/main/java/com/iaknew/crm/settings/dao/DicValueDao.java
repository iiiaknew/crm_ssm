package com.iaknew.crm.settings.dao;

import com.iaknew.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getListByCode(String code);
}
