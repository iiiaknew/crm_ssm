package com.iaknew.crm.workbench.dao;

import com.iaknew.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkDao {

    List<ClueRemark> getByClueId(String clueId);

    int delete(ClueRemark clueRemark);
}
