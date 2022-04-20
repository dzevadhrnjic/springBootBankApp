package com.example.BankApplication.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("select t from Transaction t where t.userid = ?1")
    List<Transaction> getTransactionByUserId(Long userId);

    @Query("select t from Transaction t where t.id = ?1")
    Transaction getTransactionById(Long id);

    @Query("select SUM(t.amount) AS balance from Transaction t where t.sourceaccount = ?1")
    Double sourceAccountBalance(Long id);

    @Query("select SUM(t.amount) AS balance from Transaction t where t.destinationaccount = ?1")
    Double destinationAccountBalance(Long id);

}
