package com.fabric.fabricrun.controller;

import com.fabric.fabricrun.Untility.FabricCAClient;
import com.fabric.fabricrun.Untility.FabricClient;
import com.fabric.fabricrun.Untility.UserContext;
import com.fabric.fabricrun.entity.Admin;
import com.fabric.fabricrun.entity.User;
import com.fabric.fabricrun.repository.AdminRepository;
import com.fabric.fabricrun.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.logging.type.HttpRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 注册用户
 */
@Controller
public class Register {

    @Value("${ip}")
    private String ip;
    @Value(("${adminip}"))
    private String adminip;
    @Value("${userAffiliation}")
    private String userAffiliation;
    @Value("${adminAffiliation}")
    private String adminAffiliation;
    @Value(("${userMsp}"))
    private String userMsp;
    @Value("${adminMsp}")
    private String adminMsp;
    @Value("${channelname}")
    private String channelname;
    @Value("${chaincodename}")
    private String chaincodename;
    @Value("${oderername}")
    private String oderername;
    @Value("${oderergrpc}")
    private String oderergrpc;
    @Value("${tlsOrg1Path}")
    private String tlsOrg1Path;
    @Value("${peer0org1}")
    private String peer0org1;
    @Value("${peer0org1grpc}")
    private String peer0org1grpc;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/registeruser")
    @ResponseBody
    public Map createuser(User user, HttpServletRequest request) throws Exception{
        String secret;
        UserContext userContext = new UserContext();
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String privatekey = (String) session.getAttribute("privatekey");
        FabricClient fabricClient = null;
        FabricCAClient fabricCAClient = new FabricCAClient(ip,null);
        Enrollment enrollment = fabricCAClient.enroll(username,privatekey);
        userContext.setMspId(userMsp);
        userContext.setAffiliation(userAffiliation);
        userContext.setEnrollment(enrollment);
        userContext.setName(username);
        UserContext register = new UserContext(); // 被注册人
        Optional<User> user1 = Optional.of(new User());
        user1 = userRepository.findById(user.getUsername());
        if (!user1.isPresent()) {
            //注册用户生成私钥
            register.setMspId(userMsp);
            register.setAffiliation(userAffiliation);
            register.setName(user.getUsername());
            register.setAccount(user.getName());
            FabricCAClient caClient = new FabricCAClient(ip, null);
            try {
                //区块链注册用户
                secret = caClient.register(userContext, register);
            }catch (RegistrationException e) {
                HashMap map = new HashMap();
                map.put("code",403);
                map.put("msg","No access");
                return map;
            }
            if(secret != null) {
                String salt = RandomStringUtils.randomAscii(12);
                String base = user.getPassword() +"/"+salt;
                //密码做MD5加密
                String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
                user.setPassword(md5);
                user.setSalt(salt);
                user.setPrivatekey(secret);
                //保存用户信息
                userRepository.save(user);
            }
            //获取用户证书
            enrollment = caClient.enroll(user.getUsername(),user.getPrivatekey());
            register.setEnrollment(enrollment);
            fabricClient = new FabricClient(register);
            Orderer orderer = fabricClient.getOrder(oderername,oderergrpc);
            List<Peer> peers = new ArrayList<>();
            peers.add(fabricClient.getPeer(peer0org1,peer0org1grpc,tlsOrg1Path));
            String[] args = {user.getName()};
            //用户信息上链
            Map message = fabricClient.invoke(channelname, TransactionRequest.Type.GO_LANG,chaincodename,orderer,peers,"create",args);
            message.put("code",200);
            return message;
        }else {
            HashMap map = new HashMap();
            map.put("code",0);
            map.put("msg","用户已存在");
            return map;
        }
    }


    @RequestMapping("/registeradmin")
    @ResponseBody
    public Map createAdmin(Admin admin, HttpServletRequest request) throws Exception{
        String secret;
        UserContext userContext = new UserContext();
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String privatekey = (String) session.getAttribute("privatekey");
        FabricClient fabricClient = null;
        FabricCAClient fabricCAClient = new FabricCAClient(adminip,null);
        Enrollment enrollment = fabricCAClient.enroll(username,privatekey);
        userContext.setMspId(adminMsp);
        userContext.setAffiliation(adminAffiliation);
        userContext.setEnrollment(enrollment);
        userContext.setName(username);
        UserContext register = new UserContext(); // 被注册人
        Optional<Admin> admin1 = Optional.of(new Admin());
        admin1 = adminRepository.findById(admin.getUsername());
        if (!admin1.isPresent()) {
            //注册用户生成私钥
            register.setMspId(adminMsp);
            register.setAffiliation(adminAffiliation);
            register.setName(admin.getUsername());
            register.setAccount(admin.getName());
            FabricCAClient caClient = new FabricCAClient(adminip, null);
            try {
                secret = caClient.register(userContext, register);
            }catch (RegistrationException e) {
                HashMap map = new HashMap();
                map.put("code",403);
                map.put("msg","No access");
                return map;
            }
        if(secret != null) {
                String salt = RandomStringUtils.randomAscii(12);
                String base = admin.getPassword() +"/"+salt;
                String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
                admin.setPassword(md5);
                admin.setSalt(salt);
                admin.setPrivatekey(secret);
                adminRepository.save(admin);
            }
            enrollment = caClient.enroll(admin.getUsername(),admin.getPrivatekey());
            register.setEnrollment(enrollment);
            fabricClient = new FabricClient(register);
            Orderer orderer = fabricClient.getOrder(oderername,oderergrpc);
            List<Peer> peers = new ArrayList<>();
            peers.add(fabricClient.getPeer(peer0org1,peer0org1grpc,tlsOrg1Path));
            String[] args = {admin.getName()};
            Map message = fabricClient.invoke(channelname, TransactionRequest.Type.GO_LANG,chaincodename,orderer,peers,"create",args);
            message.put("code",200);
            return message;
        }else {
            HashMap map = new HashMap();
            map.put("code",0);
            map.put("msg","用户已存在");
            return map;
        }
    }
}
