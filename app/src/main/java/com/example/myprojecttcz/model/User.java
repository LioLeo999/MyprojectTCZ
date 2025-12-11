package com.example.myprojecttcz.model;

import java.util.ArrayList;

public class User {
    protected String id;
    protected String uname;
    protected String fname;
    protected String lname;
    protected String email;
    protected String phone;
    protected String password;
    protected boolean isadmin;
    protected ArrayList<Maarchim> maarachim;

    public User(String id, String uname, String fname, String lname, String email, String phone, String password, boolean isadmin, ArrayList<Maarchim> maarachim) {
        this.id = id;
        this.uname = uname;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isadmin = isadmin;
        this.maarachim = maarachim;
    }

    public User(String id, String uname, String fname, String lname, String email, String phone, String password, boolean isadmin) {
        this.id = id;
        this.uname = uname;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isadmin = isadmin;
        this.maarachim = new ArrayList<Maarchim>();
    }

    public User(String uname, String fname, String lname) {
        this.uname = uname;
        this.fname = fname;
        this.lname = lname;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Maarchim> getMaarachim() {
        return maarachim;
    }

    public void setMaarachim(ArrayList<Maarchim> maarachim) {
        this.maarachim = maarachim;
    }

    public boolean isIsadmin() {
        return isadmin;
    }

    public void setIsadmin(boolean isadmin) {
        this.isadmin = isadmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", uname='" + uname + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", isadmin=" + isadmin +
                ", maarachim=" + maarachim +
                '}';
    }
}
