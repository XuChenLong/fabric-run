package com.fabric.fabricrun.repository;

import com.fabric.fabricrun.entity.ApplyPK;
import com.fabric.fabricrun.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RecordPepository extends JpaRepository<Record, ApplyPK> {
    @Transactional
    Record findRecordByAdminAndUserAndProperty(String admin, String user, String property);
    @Transactional
    void deleteRecordByAdminAndUserAndProperty(String admin, String user, String property);
    List<Record> findRecordsByAdmin(String admin);
}
