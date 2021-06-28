package com.fabric.fabricrun.repository;

import com.fabric.fabricrun.entity.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin,String> {

    @Transactional()
    @Modifying(clearAutomatically = true)
    @Query(value = "update super_admin set password = ?2 , salt = ?3 where username = ?1",nativeQuery = true)
    int updatePasswordAndSaltbyId(String username,String password, String salt);
}
