package com.fabric.fabricrun.repository;

import com.fabric.fabricrun.entity.Admin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//Repository<数据类，主键数据类型>
public interface AdminRepository extends JpaRepository<Admin,String> {
    @Query(value = "select username,name,state from Admin")
    List findAllName();

    @Query(value = "select username,name,state from Admin")
    List find(Pageable pageable);

    @Transactional()
    @Modifying(clearAutomatically = true)
    @Query(value = "update admin set state = ?2 where username = ?1",nativeQuery = true)
    int updatestatebyId(String username, boolean state);

    @Transactional()
    @Modifying(clearAutomatically = true)
    @Query(value = "update admin set password = ?2 , salt = ?3 where username = ?1",nativeQuery = true)
    int updatePasswordAndSaltbyId(String username,String password, String salt);
}

