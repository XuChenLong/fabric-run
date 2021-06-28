package com.fabric.fabricrun.entity;

import javax.persistence.*;

@Entity
@IdClass(ApplyPK.class)
public class Record {

    @Id
    private String admin;

    @Id
    private String user;

    @Id
    private String property;

    @Column(nullable = false)
    private int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
