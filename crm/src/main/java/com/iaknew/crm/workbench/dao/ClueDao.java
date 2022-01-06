package com.iaknew.crm.workbench.dao;

import com.iaknew.crm.workbench.domain.Clue;

public interface ClueDao {
    int save(Clue clue);

    Clue getClueById(String id);

    Clue getById(String clueId);

    int deleteById(String clueId);
}
