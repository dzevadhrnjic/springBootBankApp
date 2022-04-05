package com.example.BankApplication.user;

public class AccessToken {

    TokenUtil tokenUtil = new TokenUtil();

    private String accessToken;

    public AccessToken(String jwt) {
    }

    public AccessToken() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
