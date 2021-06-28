package com.fabric.fabricrun.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ApplyPK implements Serializable {

    private String admin;

    private String user;

    private String property;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplyPK applyPK = (ApplyPK) o;
        return Objects.equals(admin, applyPK.admin) &&
                Objects.equals(user, applyPK.user) &&
                Objects.equals(property, applyPK.property);
    }

    @Override
    public int hashCode() {
        return Objects.hash(admin, user, property);
    }
}
