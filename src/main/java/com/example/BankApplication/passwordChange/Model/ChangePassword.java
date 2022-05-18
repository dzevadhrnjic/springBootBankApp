package com.example.BankApplication.passwordChange.Model;

import javax.persistence.Table;

@Table(name = "dbuser")
public class ChangePassword {

    private String code;
    private String password;
    public ChangePassword() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
