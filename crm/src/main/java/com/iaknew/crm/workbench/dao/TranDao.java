package com.iaknew.crm.workbench.dao;

import com.iaknew.crm.workbench.domain.Tran;

public interface TranDao {

    int save(Tran tran);

    Tran detail(String id);

    int changeStage(Tran t);
}
