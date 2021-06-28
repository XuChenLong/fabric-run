package com.fabric.fabricrun.controller;

import com.fabric.fabricrun.entity.User;
import com.fabric.fabricrun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/*
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/user/{id}")
    public User getuser(@PathVariable("id") String username) {
        User user = userRepository.getOne(username);
        return user;
    }

    @GetMapping("/user")
    public User insertUser(User user) {
        User save = userRepository.save(user);
        return save;
    }
}
*/