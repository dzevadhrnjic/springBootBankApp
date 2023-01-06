package com.example.BankApplication.user.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.BankApplication.blacklist.service.BlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
@Service
public class TokenUtil {

    @Autowired
    BlacklistService blacklistService;

    public String jwt(Long userId) {

        Algorithm algorithm = Algorithm.HMAC512("secret");
        String token = JWT.create()
                .withIssuer("auth0")
                .withSubject(String.valueOf(userId))
                .sign(algorithm);
        return token;
    }

    public Long verifyJwt(String token) {

        blacklistService.blackListOfTokens(token);

        Algorithm algorithm = Algorithm.HMAC512("secret");

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();

        DecodedJWT jwt = verifier.verify(token);

        Long subject = Long.valueOf(jwt.getSubject());

        return subject;
    }
}
