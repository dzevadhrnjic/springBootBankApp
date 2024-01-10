package com.example.BankApplication.blacklist.controller;

import com.example.BankApplication.blacklist.exception.BlackListTokenException;
import com.example.BankApplication.blacklist.model.BlackList;
import com.example.BankApplication.blacklist.service.BlacklistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletResponse;
import java.util.Map;

@RestController
@RequestMapping(path = "api/blacklist")
public class BlacklistController {

    private final BlacklistService blacklistService;

    public BlacklistController(BlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    @PostMapping(path = "logout")
    public ResponseEntity<Object> logout(@RequestHeader(value = "Authorization") String token){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(blacklistService.logout(token));
        }catch (BlackListTokenException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(path = "tokens")
    public ResponseEntity<Object> blackList(@RequestHeader(value = "Authorization") String token){
        try {
            blacklistService.blackListOfTokens(token);
            return ResponseEntity.status(HttpStatus.OK).body(token);
        }catch (BlackListTokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
