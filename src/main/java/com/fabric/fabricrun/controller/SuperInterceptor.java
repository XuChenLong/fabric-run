package com.fabric.fabricrun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class SuperInterceptor {

    @RequestMapping("/Super_signout")
    public String signout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "SuperAdmin/login";
    }

    @RequestMapping("/Super_Message")
    public String Super_Message(HttpServletRequest request) {
        request.getSession().invalidate();
        return "SuperAdmin/user-look";
    }

    @RequestMapping("/SLogin")
    public String SLogin() {
        return "SuperAdmin/login";
    }

    @RequestMapping("/Superindex")
    public String Superindex(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if ("superAdmin".equals(session.getAttribute("role"))) {
            return "SuperAdmin/admin-user";
        }else {
            return "S403";
        }
    }

    @RequestMapping("/Super_admin")
    public String Super_admin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if ("superAdmin".equals(session.getAttribute("role"))) {
            return "SuperAdmin/admin-admin";
        }else {
            return "S403";
        }
    }

    @RequestMapping("/Super_add")
    public String Super_add(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if ("superAdmin".equals(session.getAttribute("role"))) {
            return "SuperAdmin/user-add";
        }else {
            return "S403";
        }
    }

    @RequestMapping("/Super_editpw")
    public String Super_editpw(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if ("superAdmin".equals(session.getAttribute("role"))) {
            return "SuperAdmin/user-editpw";
        }else {
            return "S403";
        }
    }

    @RequestMapping("/S_editpw")
    public String S_editpw(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if ("superAdmin".equals(session.getAttribute("role"))) {
            return "SuperAdmin/editpw";
        }else {
            return "S403";
        }
    }

}
