package com.fabric.fabricrun.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SuperAdmin {

    @Id
    private String username;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String privatekey;
    @Column(nullable = false,length = 12)
    private String salt;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivatekey() {
        return privatekey;
    }

    public void setPrivatekey(String privatekey) {
        this.privatekey = privatekey;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
