package com.fabric.fabricrun.controller;

import com.fabric.fabricrun.Untility.FabricCAClient;
import com.fabric.fabricrun.Untility.FabricClient;
import com.fabric.fabricrun.Untility.UserContext;
import com.fabric.fabricrun.entity.Admin;
import com.fabric.fabricrun.entity.SuperAdmin;
import com.fabric.fabricrun.entity.User;
import com.fabric.fabricrun.repository.AdminRepository;
import com.fabric.fabricrun.repository.SuperAdminRepository;
import com.fabric.fabricrun.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Controller
public class SuperLogin {
    @Value("${ip}")
    String ip;
    @Value("${adminip}")
    String adminip;
    @Value("${userMsp}")
    String userMsp;
    @Value("${userAffiliation}")
    String userAffiliation;
    @Value("${adminMsp}")
    String adminMsp;
    @Value("${tlsOrg1Path}")
    String tlsOrg1Path;
    @Autowired
    UserRepository userRepository; //用户数据类
    @Autowired
    SuperAdminRepository superAdminRepository;

    @ResponseBody
    @PostMapping(value = "/superlogin")
    public void login(
            @RequestParam("username") String username,
            @RequestParam("password") String pwd,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        int msg;
        String name;
        HttpSession session = request.getSession();
        Optional<SuperAdmin> superAdmin = superAdminRepository.findById(username);
        if (!superAdmin.isPresent()) {
        response.getWriter().write("用户名不存在");
            return;
        }
        SuperAdmin superAdmin1 =superAdmin.get();
        String salt = superAdmin1.getSalt();
        String password = superAdmin1.getPassword();
        String base = pwd+"/"+salt;
        name = superAdmin1.getName();
        if(DigestUtils.md5DigestAsHex(base.getBytes()).equals(password)) {
            msg = 200;
        }else {
            msg = 403;
        }
        session.setAttribute("privatekey",superAdmin1.getPrivatekey());
        session.setAttribute("username",superAdmin1.getUsername());
        session.setAttribute("role","superAdmin");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write("{\"code\":"+msg+",\"user\":{\"loginname\":\""+name+"\"}}");
    }

}
