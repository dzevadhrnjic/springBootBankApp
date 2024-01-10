package com.example.BankApplication.blacklist.service;

import com.example.BankApplication.blacklist.database.BlacklistRepository;
import com.example.BankApplication.blacklist.exception.BlackListTokenException;
import com.example.BankApplication.blacklist.model.BlackList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlacklistService {


    @Autowired
    BlacklistRepository blacklistRepository;

    public BlackList logout (String token) {

        BlackList blackList = new BlackList();

        blackList.setToken(token);
        blacklistRepository.save(blackList);

        return blackList;
    }

    public void blackListOfTokens(String token) {

        BlackList checkToken = blacklistRepository.checkIfTokenIsInBlackList(token);

        if (checkToken != null) {
            throw new BlackListTokenException("Token is in blacklist, unauthorized user");
        }
    }
}
