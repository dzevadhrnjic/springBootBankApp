package com.example.BankApplication.user.Service;

import com.example.BankApplication.user.Database.UserRepository;
import com.example.BankApplication.user.Exception.InvalidEmailOrPasswordException;
import com.example.BankApplication.user.Model.AccessToken;
import com.example.BankApplication.user.Model.User;
import com.example.BankApplication.user.Model.UserLogin;
import com.example.BankApplication.user.Util.HashUtils;
import com.example.BankApplication.user.Util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    HashUtils hashUtils = new HashUtils();
    TokenUtil tokenUtil = new TokenUtil();

    @Autowired
    UserRepository userRepository;

    public AccessToken loginUser(UserLogin userLogin) {

        AccessToken accessToken = new AccessToken();

        User loginUser = userRepository.getUser(userLogin.getEmail(),
                hashUtils.generateHash(userLogin.getPassword()));

        if (loginUser == null) {
            throw new InvalidEmailOrPasswordException("Invalid email or password, try again");
        } else {
            Long userId = loginUser.getId();
            accessToken.setAccessToken(tokenUtil.jwt(userId));
        }
        return accessToken;
    }
}