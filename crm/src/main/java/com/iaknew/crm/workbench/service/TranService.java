package com.iaknew.crm.workbench.service;

import com.iaknew.crm.workbench.domain.Tran;
import com.iaknew.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranService {
    List<String> getCustomerName(String name);

    boolean save(String customerName, Tran tran);

    Tran detail(String id);

    List<TranHistory> getTranHistoryList(String tranId);

    boolean changeStage(Tran t);
}
