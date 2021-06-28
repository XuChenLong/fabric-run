package com.fabric.fabricrun.entity;

public class Property {
    private String id;
    private String name;
    private int num;
    private int maxnum;

    public int getMaxnum() {
        return maxnum;
    }

    public void setMaxnum(int maxnum) {
        this.maxnum = maxnum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
