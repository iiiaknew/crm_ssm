package com.iaknew.crm.workbench.dao;

import com.iaknew.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryDao {

    int save(TranHistory tranHistory);

    List<TranHistory> getTranHistoryList(String tranId);
}
