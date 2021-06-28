package com.fabric.fabricrun.controller;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.DigestUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
public class Test {

    @Autowired
    DataSource dataSource;

    @RequestMapping(value="/hello", method=RequestMethod.GET)
    public String index() throws SQLException {
        String sql = "SELECT username,name,pwd,state FROM user WHERE username = ?";
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
        connection.close();
        return sql;
    }

    //md5
    @RequestMapping(value="/md5", method=RequestMethod.GET)
    public String md5() {
        String str="adminpw";
        String salt = RandomStringUtils.randomAscii(12);
        String base = str +"/"+ salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        System.out.println(base);
        System.out.println(base.getBytes());
        System.out.println(md5);
        return md5;
    }
}