package com.fabric.fabricrun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class Interceptor {

    @RequestMapping("/User_signout")
    public String signout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "User/login";
    }

    @RequestMapping("/Login")
    public String Login() {
        return "User/login";
    }

    @RequestMapping("/Userindex")
    public String AdminIndex(HttpServletRequest request) {
        if("User".equals(request.getSession().getAttribute("role"))) {
            return "User/myproperty-look";
        }else {
            return "403";
        }
    }

    @RequestMapping("/User_apply")
    public String User_apply(HttpServletRequest request) {
        if("User".equals(request.getSession().getAttribute("role"))) {
            return "User/buy-apply";
        }else {
            return "403";
        }
    }

    @RequestMapping("/User_property_look")
    public String User_property_look(HttpServletRequest request) {
        if("User".equals(request.getSession().getAttribute("role"))) {
            return "User/property-admin";
        }else {
            return "403";
        }
    }

    @RequestMapping("/User_property_Mess")
    public String User_property_Mess(HttpServletRequest request) {
        if("User".equals(request.getSession().getAttribute("role"))) {
            return "User/property-Mess";
        }else {
            return "403";
        }
    }

    @RequestMapping("/User_Message")
    public String User_Message(HttpServletRequest request) {
        if("User".equals(request.getSession().getAttribute("role"))) {
            return "User/user-look";
        }else {
            return "403";
        }
    }

    @RequestMapping("/U_editpw")
    public String U_editpw(HttpServletRequest request) {
        if("User".equals(request.getSession().getAttribute("role"))) {
            return "User/editpw";
        }else {
            return "403";
        }
    }
}
