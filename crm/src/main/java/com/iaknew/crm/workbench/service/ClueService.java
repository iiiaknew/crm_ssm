package com.iaknew.crm.workbench.service;

import com.iaknew.crm.workbench.domain.Clue;
import com.iaknew.crm.workbench.domain.Tran;

public interface ClueService {
    boolean save(Clue clue);

    Clue getClueById(String id);

    boolean unbound(String id);

    boolean bound(String clueId, String[] aids);

    boolean convert(String clueId, Tran tran, String createBy);
}
