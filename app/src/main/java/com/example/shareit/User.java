package com.example.shareit;

public class User {

    private String name, phone, email, usertype, userId;
    private Boolean verification;

    public User() {
    }

    public User(String name, String phone, String email, String usertype, String userId, Boolean verification) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.usertype = usertype;
        this.userId = userId;
        this.verification = verification;
    }

    public Boolean getVerification() {
        return verification;
    }

    public void setVerification(Boolean verification) {
        this.verification = verification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsertype() {
        return usertype;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }
}
