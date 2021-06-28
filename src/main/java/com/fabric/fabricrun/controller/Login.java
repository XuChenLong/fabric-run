package com.fabric.fabricrun.controller;

import com.fabric.fabricrun.Untility.FabricCAClient;
import com.fabric.fabricrun.Untility.FabricClient;
import com.fabric.fabricrun.Untility.UserContext;
import com.fabric.fabricrun.entity.Admin;
import com.fabric.fabricrun.entity.User;
import com.fabric.fabricrun.repository.AdminRepository;
import com.fabric.fabricrun.repository.UserRepository;
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
public class Login {
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
    AdminRepository adminRepository;

    @ResponseBody
    @PostMapping(value = "/login")
    public void login(
            @RequestParam("username") String username,
            @RequestParam("password") String pwd,
            @RequestParam("usertype") String role,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws EnrollmentException, InvalidArgumentException, IllegalAccessException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CryptoException, TransactionException, IOException {
        int msg;
        String name;
        Boolean state;
        FabricClient fabricClient = null;
        List<Peer> peers = new ArrayList<>();
        Orderer orderer;
        UserContext userContext= new UserContext();
        HttpSession session = request.getSession();
        response.setCharacterEncoding("utf-8");
        if ("管理员登录".equals(role)) {
            FabricCAClient fabricCAClient = new FabricCAClient(adminip,null);
            Optional<Admin> admin1 = adminRepository.findById(username);
            if (!admin1.isPresent()) {
                response.getWriter().write("用户名不存在");
                return;
            }
            Admin admin =admin1.get();
            String salt = admin.getSalt();
            String password = admin.getPassword();
            String base = pwd+"/"+salt;
            state = admin.isState();
            if(!state) {
                response.getWriter().write("{\"code\":400}");
                return;
            }
            name = admin.getName();
            if(DigestUtils.md5DigestAsHex(base.getBytes()).equals(password)) {
                Enrollment enrollment = fabricCAClient.enroll(admin.getUsername(),admin.getPrivatekey());
                userContext.setName(admin.getUsername());
                userContext.setAccount(admin.getName());
                userContext.setMspId(adminMsp);
                userContext.setAffiliation(userAffiliation);
                userContext.setEnrollment(enrollment);
                fabricClient = new FabricClient(userContext);
                orderer = fabricClient.getOrder("orderer.example.com", "grpcs://orderer.example.com:7050");
                peers.add(fabricClient.getPeer("peer0.org1.example.com", "grpcs://peer0.org1.example.com:7051", tlsOrg1Path));
                fabricClient.addchannel(peers,orderer,"mychannel");
                session.setAttribute("role","Admin");
            }
        }else {
            FabricCAClient fabricCAClient = new FabricCAClient(ip,null);
            Optional<User> user1 = userRepository.findById(username);
            if (!user1.isPresent()) {
                response.getWriter().write("用户名不存在");
                return;
            }
            User user =user1.get();
            String salt = user.getSalt();
            String password = user.getPassword();
            String base = pwd+"/"+salt;
            name = user.getName();
            state = user.isState();
            if(!state) {
                response.getWriter().write("{\"code\":400}");
                return;
            }
            if(DigestUtils.md5DigestAsHex(base.getBytes()).equals(password)) {
                Enrollment enrollment = fabricCAClient.enroll(user.getUsername(),user.getPrivatekey());
                userContext.setName(user.getUsername());
                userContext.setAccount(user.getName());
                userContext.setMspId(userMsp);
                userContext.setAffiliation(userAffiliation);
                userContext.setEnrollment(enrollment);
                fabricClient = new FabricClient(userContext);
                orderer = fabricClient.getOrder("orderer.example.com", "grpcs://orderer.example.com:7050");
                peers.add(fabricClient.getPeer("peer0.org1.example.com", "grpcs://peer0.org1.example.com:7051", tlsOrg1Path));
                fabricClient.addchannel(peers,orderer,"mychannel");
                session.setAttribute("role","User");
            }
        }
        if(fabricClient == null) {
            msg = 403;
        }else {
            msg = 200;
            session.setAttribute("userContext",userContext);
            session.setAttribute("fabricClient",fabricClient);
        }
        response.getWriter().write("{\"code\":"+msg+",\"user\":{\"loginname\":\""+name+"\"}}");
        return;
    }
}
