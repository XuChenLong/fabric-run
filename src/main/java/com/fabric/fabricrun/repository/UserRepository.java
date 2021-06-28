package com.fabric.fabricrun.repository;

import com.fabric.fabricrun.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//继承JpaRepository使用repository操作数据表
public interface UserRepository extends JpaRepository<User,String> {
    @Query(value = "select username,name,state from User")
    List find(Pageable pageable);

    @Transactional()
    @Modifying(clearAutomatically = true)
    @Query(value = "update user set state = ?2 where username = ?1",nativeQuery = true)
    int updatestatebyId(String username, boolean state);

    @Transactional()
    @Modifying(clearAutomatically = true)
    @Query(value = "update user set password = ?2 , salt = ?3 where username = ?1",nativeQuery = true)
    int updatePasswordAndSaltbyId(String username,String password, String salt);
}
