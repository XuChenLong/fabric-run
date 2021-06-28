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
import org.apache.commons.lang3.RandomStringUtils;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.RevocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

@Controller
public class Usermessage {

    @Value("${ip}")
    private String ip;
    @Value("${userAffiliation}")
    private String userAffiliation;
    @Value("${adminAffiliation}")
    private String adminAffiliation;
    @Value(("${userMsp}"))
    private String userMsp;
    @Value("${adminMsp}")
    private String adminMsp;
    @Autowired
    UserRepository userRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    SuperAdminRepository superAdminRepository;

    @GetMapping("/users")
    @ResponseBody
    public List<User> users(@RequestParam("currentPage") int currentPage) {
        Sort sort = Sort.by(Sort.Direction.ASC, "username");
        Pageable pageable = PageRequest.of(currentPage, 10, sort);
        List<User> users = userRepository.find(pageable);
        return users;
    }

    @PostMapping("/userscount")
    @ResponseBody
    public int userscount(){
        int count;
        count = (int) userRepository.count();
        return count;
    }

    @GetMapping("/admins")
    @ResponseBody
    public List<Admin> admins(@RequestParam("currentPage") int currentPage) {
        Sort sort = Sort.by(Sort.Direction.ASC, "username");
        Pageable pageable = PageRequest.of(currentPage, 10, sort);
        List<Admin> admins = adminRepository.find(pageable);
        return admins;
    }

    @PostMapping("/adminscount")
    @ResponseBody
    public int adminscount(){
        int count;
        count = (int) adminRepository.count();
        return count;
    }

    @PostMapping("/Superadmin/updateAdmin")
    @ResponseBody
    public String updateAdmin(HttpServletRequest request,@RequestParam("username") String username,@RequestParam("password") String password) throws IllegalAccessException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, MalformedURLException, CryptoException, EnrollmentException, InvalidArgumentException {
        HttpSession session = request.getSession();
        String suepername = (String) session.getAttribute("username");
        String privatekey = (String) session.getAttribute("privatekey");
        FabricCAClient fabricCAClient = new FabricCAClient(ip,null);
        try {
            fabricCAClient.enroll(suepername,privatekey);
        }catch(EnrollmentException e) {
            return "无权修改";
        }
        String salt = RandomStringUtils.randomAscii(12);
        String base = password +"/"+salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        int state = adminRepository.updatePasswordAndSaltbyId(username,md5,salt);
        if(state!=0) {
            return "修改管理员密码成功";
        }else {
            return "账户名不存在";
        }
    }

    @PostMapping("/Superadmin/updateuser")
    @ResponseBody
    public String updateuser(HttpServletRequest request,@RequestParam("username") String username,@RequestParam("password") String password) throws IllegalAccessException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, MalformedURLException, CryptoException, EnrollmentException, InvalidArgumentException {
        HttpSession session = request.getSession();
        String suepername = (String) session.getAttribute("username");
        String privatekey = (String) session.getAttribute("privatekey");
        FabricCAClient fabricCAClient = new FabricCAClient(ip,null);
        try {
            fabricCAClient.enroll(suepername,privatekey);
        }catch(EnrollmentException e) {
            return "msg:无权修改";
        }
        String salt = RandomStringUtils.randomAscii(12);
        String base = password +"/"+salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        int state = userRepository.updatePasswordAndSaltbyId(username,md5,salt);
        if(state!=0) {
            return "修改管理员密码成功";
        }else {
            return "账户名不存在";
        }
    }

    @PostMapping("/stopuser")
    @ResponseBody
    public String stopuser(HttpServletRequest request, @RequestParam("username") String username) throws EnrollmentException, InvalidArgumentException, IllegalAccessException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, MalformedURLException, CryptoException, RevocationException {
        UserContext superContext = new UserContext();
        HttpSession session = request.getSession();
        Enrollment enrollment;
        String suepername = (String) session.getAttribute("username");
        String privatekey = (String) session.getAttribute("privatekey");
        FabricCAClient fabricCAClient = new FabricCAClient(ip,null);
        try {
            enrollment = fabricCAClient.enroll(suepername,privatekey);
        }catch (Exception e) {
            return "无权操作";
        }
        superContext.setMspId(userMsp);
        superContext.setAffiliation(userAffiliation);
        superContext.setEnrollment(enrollment);
        superContext.setName(username);
        int state;
        state = userRepository.updatestatebyId(username,false);
        if (state!=0) {
            return "停用用户成功";
        }else {
            return "当前用户不存在";
        }
    }

    @PostMapping("/enableuser")
    @ResponseBody
    public String enableuser(HttpServletRequest request, @RequestParam("username") String username) throws EnrollmentException, InvalidArgumentException, IllegalAccessException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, MalformedURLException, CryptoException, RevocationException {
        UserContext superContext = new UserContext();
        HttpSession session = request.getSession();
        Enrollment enrollment;
        String suepername = (String) session.getAttribute("username");
        String privatekey = (String) session.getAttribute("privatekey");
        FabricCAClient fabricCAClient = new FabricCAClient(ip,null);
        try {
            enrollment = fabricCAClient.enroll(suepername,privatekey);
        }catch (Exception e) {
            return "无权操作";
        }
        superContext.setMspId(userMsp);
        superContext.setAffiliation(userAffiliation);
        superContext.setEnrollment(enrollment);
        superContext.setName(username);
        int state;
        state = userRepository.updatestatebyId(username,true);
        if (state!=0) {
            return "启用用户成功";
        }else {
            return "当前用户不存在";
        }
    }

    @PostMapping("/stopadmin")
    @ResponseBody
    public String stopadmin(HttpServletRequest request, @RequestParam("username") String username) throws EnrollmentException, InvalidArgumentException, IllegalAccessException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, MalformedURLException, CryptoException, RevocationException {
        UserContext superContext = new UserContext();
        HttpSession session = request.getSession();
        Enrollment enrollment;
        String suepername = (String) session.getAttribute("username");
        String privatekey = (String) session.getAttribute("privatekey");
        FabricCAClient fabricCAClient = new FabricCAClient(ip,null);
        try {
            enrollment = fabricCAClient.enroll(suepername,privatekey);
        }catch (Exception e) {
            return "无权操作";
        }
        superContext.setMspId(adminMsp);
        superContext.setAffiliation(adminAffiliation);
        superContext.setEnrollment(enrollment);
        superContext.setName(username);
        int state;
        state = adminRepository.updatestatebyId(username,false);
        if (state!=0) {
            return "停用用户成功";
        }else {
            return "当前用户不存在";
        }
    }

    @PostMapping("/enableadmin")
    @ResponseBody
    public String enableadmin(HttpServletRequest request, @RequestParam("username") String username) throws EnrollmentException, InvalidArgumentException, IllegalAccessException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, MalformedURLException, CryptoException, RevocationException {
        UserContext superContext = new UserContext();
        HttpSession session = request.getSession();
        Enrollment enrollment;
        String suepername = (String) session.getAttribute("username");
        String privatekey = (String) session.getAttribute("privatekey");
        FabricCAClient fabricCAClient = new FabricCAClient(ip,null);
        try {
            enrollment = fabricCAClient.enroll(suepername,privatekey);
        }catch (Exception e) {
            return "无权操作";
        }
        superContext.setMspId(adminMsp);
        superContext.setAffiliation(adminAffiliation);
        superContext.setEnrollment(enrollment);
        superContext.setName(username);
        int state;
        state = adminRepository.updatestatebyId(username,true);
        if (state!=0) {
            return "启用用户成功";
        }else {
            return "当前用户不存在";
        }
    }

    @PostMapping("/deleteuser")
    @ResponseBody
    public String deleteuser(HttpServletRequest request, @RequestParam("username") String username) throws EnrollmentException, InvalidArgumentException, IllegalAccessException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, MalformedURLException, CryptoException, RevocationException {
        UserContext superContext = new UserContext();
        HttpSession session = request.getSession();
        Enrollment enrollment;
        String suepername = (String) session.getAttribute("username");
        String privatekey = (String) session.getAttribute("privatekey");
        FabricCAClient fabricCAClient = new FabricCAClient(ip,null);
        try {
            enrollment = fabricCAClient.enroll(suepername,privatekey);
        }catch (Exception e) {
            return "无权操作";
        }
        superContext.setMspId(userMsp);
        superContext.setAffiliation(userAffiliation);
        superContext.setEnrollment(enrollment);
        superContext.setName("admin");
        try{
            userRepository.deleteById(username);
        }catch(IllegalArgumentException e) {
            return "当前用户不存在";
        }
        fabricCAClient.revoke(superContext,username,"cessationOfOperation");
        return "删除用户成功";
    }

    @PostMapping("/deleteadmin")
    @ResponseBody
    public String deleteadmin(HttpServletRequest request, @RequestParam("username") String username) throws EnrollmentException, InvalidArgumentException, IllegalAccessException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, MalformedURLException, CryptoException, RevocationException {
        UserContext superContext = new UserContext();
        HttpSession session = request.getSession();
        String suepername = (String) session.getAttribute("username");
        String privatekey = (String) session.getAttribute("privatekey");
        FabricCAClient fabricCAClient = new FabricCAClient(ip,null);
        Enrollment enrollment = fabricCAClient.enroll(suepername,privatekey);
        superContext.setMspId(adminMsp);
        superContext.setAffiliation(adminAffiliation);
        superContext.setEnrollment(enrollment);
        superContext.setName("admin");
        try{
            adminRepository.deleteById(username);
        }catch(IllegalArgumentException e) {
            return "当前管理员不存在";
        }
        fabricCAClient.revoke(superContext,username,"cessationOfOperation");
        return "删除管理员成功";
    }

    @PostMapping("/User_updatepw")
    @ResponseBody
    public String User_updatepw(HttpServletRequest request,@RequestParam("npwd") String npwd,@RequestParam("pwd") String pwd,@RequestParam("pwd1") String pwd1) throws IllegalAccessException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, MalformedURLException, CryptoException, EnrollmentException, InvalidArgumentException {
        HttpSession session = request.getSession();
        UserContext userContext = (UserContext) session.getAttribute("userContext");
        String username = userContext.getName();
        Optional<User> user1 = userRepository.findById(username);
        if (!user1.isPresent()) {
            return "用户名不存在";
        }
        if(!pwd.equals(pwd1)) {
            return "密码不一致";
        }
        User user =user1.get();
        String salt = user.getSalt();
        String password = user.getPassword();
        String base = npwd+"/"+salt;
        if(DigestUtils.md5DigestAsHex(base.getBytes()).equals(password)) {
            salt = RandomStringUtils.randomAscii(12);
            base = pwd +"/"+salt;
            String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
            int state = userRepository.updatePasswordAndSaltbyId(username,md5,salt);
            if(state != 0) {
                return "账户密码修改成功";
            }else {
                return "当前账户不存在";
            }
        }else {
            return "原密码错误";
        }
    }

    @PostMapping("/Admin_updatepw")
    @ResponseBody
    public String Admin_updatepw(HttpServletRequest request,@RequestParam("npwd") String npwd,@RequestParam("pwd") String pwd,@RequestParam("pwd1") String pwd1) throws IllegalAccessException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, MalformedURLException, CryptoException, EnrollmentException, InvalidArgumentException {
        HttpSession session = request.getSession();
        UserContext userContext = (UserContext) session.getAttribute("userContext");
        String username = userContext.getName();
        Optional<Admin> admin1 = adminRepository.findById(username);
        if (!admin1.isPresent()) {
            return "用户名不存在";
        }
        if(!pwd.equals(pwd1)) {
            return "密码不一致";
        }
        Admin admin =admin1.get();
        String salt = admin.getSalt();
        String password = admin.getPassword();
        String base = npwd+"/"+salt;
        if(DigestUtils.md5DigestAsHex(base.getBytes()).equals(password)) {
            salt = RandomStringUtils.randomAscii(12);
            base = pwd +"/"+salt;
            String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
            int state = adminRepository.updatePasswordAndSaltbyId(username,md5,salt);
            if(state != 0) {
                return "账户密码修改成功";
            }else {
                return "当前账户不存在";
            }
        }else {
            return "原密码错误";
        }
    }

    @PostMapping("/Super_updatepw")
    @ResponseBody
    public String Super_updatepw(HttpServletRequest request,@RequestParam("npwd") String npwd,@RequestParam("pwd") String pwd,@RequestParam("pwd1") String pwd1) throws IllegalAccessException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, MalformedURLException, CryptoException, EnrollmentException, InvalidArgumentException {
        HttpSession session = request.getSession();
        String username = (String)session.getAttribute("username");
        Optional<SuperAdmin> admin1 = superAdminRepository.findById(username);
        if (!admin1.isPresent()) {
            return "用户名不存在";
        }
        if(!pwd.equals(pwd1)) {
            return "密码不一致";
        }
        SuperAdmin superAdmin =admin1.get();
        String salt = superAdmin.getSalt();
        String password = superAdmin.getPassword();
        String base = npwd+"/"+salt;
        if(DigestUtils.md5DigestAsHex(base.getBytes()).equals(password)) {
            salt = RandomStringUtils.randomAscii(12);
            base = pwd +"/"+salt;
            String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
            int state = superAdminRepository.updatePasswordAndSaltbyId(username,md5,salt);
            if(state != 0) {
                return "账户密码修改成功";
            }else {
                return "当前账户不存在";
            }
        }else {
            return "原密码错误";
        }
    }
}
