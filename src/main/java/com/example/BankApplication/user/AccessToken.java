package com.example.BankApplication.user;

public class AccessToken {

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
