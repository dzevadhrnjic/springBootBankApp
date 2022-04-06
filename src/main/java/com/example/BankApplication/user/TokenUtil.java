package com.example.BankApplication.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenUtil {

    public static void main(String[] args) {
//        System.out.println(jwt(80L));
//        System.out.println(verifyJwt("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4MCIsImlzcyI6ImF1dGgwIn0.I1tY_n9Ozo1iege1YZhsSyHtNaqPO-_4hIp9NFDTaFXNdPaBFgsw7dNLQ3RdV5gkw7CA4X4QOweZ9WLWrH-1bA"));
    }
    public  String jwt(Long userId) {

        Algorithm algorithm = Algorithm.HMAC512("secret");
        String token = JWT.create()
                .withIssuer("auth0")
                .withSubject(String.valueOf(userId))
                .sign(algorithm);

        return token;
    }

    public  Long verifyJwt(String token){

        Algorithm algorithm = Algorithm.HMAC512("secret");

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();

        DecodedJWT jwt = verifier.verify(token);

        Long subject = Long.valueOf(jwt.getSubject());

        return subject;
    }
}
