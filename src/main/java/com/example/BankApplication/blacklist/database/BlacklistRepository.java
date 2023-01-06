package com.example.BankApplication.blacklist.database;

import com.example.BankApplication.blacklist.model.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlacklistRepository extends JpaRepository<BlackList, Long> {

    @Query(value = "SELECT * FROM blacklist WHERE token = ?", nativeQuery = true)
    public BlackList checkIfTokenIsInBlackList(String token);
}
