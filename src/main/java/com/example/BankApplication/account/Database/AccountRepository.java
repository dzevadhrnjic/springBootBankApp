package com.example.BankApplication.account.Database;

import com.example.BankApplication.account.Model.Account;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAll(Specification<Account> specification, Pageable paging);

    @Query("select a from Account a where a.userid = ?1")
    List<Account> getAccountByUserId(Long id);

    @Query("select a from Account a where a.userid = ?1 and a.id = ?2")
    Account getAccountByUserIdAndUserId(Long userId, Long id);

    @Query("select a from Account a where a.id = ?1")
    Account getAccountId(Long id);

    @Modifying
    @Query("delete from Account a where a.id = ?1 and a.userid = ?2")
    void deleteAccount(Long id, Long userId);
}
