package com.fabric.fabricrun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AdminInterceptor {

    @RequestMapping("/Admin_signout")
    public String signout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "User/login";
    }

    @RequestMapping("/Adminindex")
    public String AdminIndex(HttpServletRequest request) {
        if("Admin".equals(request.getSession().getAttribute("role"))) {
            return "Admin/admin-user";
        }else {
            return "403";
        }
    }

    @RequestMapping("/Admin_property_add")
    public String Admin_property_add(HttpServletRequest request) {
        if("Admin".equals(request.getSession().getAttribute("role"))) {
            return "Admin/property-add";
        }else {
            return "403";
        }
    }

    @RequestMapping("/Admin_apply_look")
    public String Admin_apply_look(HttpServletRequest request) {
        if("Admin".equals(request.getSession().getAttribute("role"))) {
            return "Admin/apply-look";
        }else {
            return "403";
        }
    }

    @RequestMapping("/Admin_property")
    public String Admin_property(HttpServletRequest request) {
        if("Admin".equals(request.getSession().getAttribute("role"))) {
            return "Admin/property-admin";
        }else {
            return "403";
        }
    }

    @RequestMapping("/Admin_Message")
    public String Admin_Message(HttpServletRequest request) {
        if("Admin".equals(request.getSession().getAttribute("role"))) {
            return "Admin/user-look";
        }else {
            return "403";
        }
    }

    @RequestMapping("/A_editpw")
    public String A_editpw(HttpServletRequest request) {
        if("Admin".equals(request.getSession().getAttribute("role"))) {
            return "Admin/editpw";
        }else {
            return "403";
        }
    }
}
