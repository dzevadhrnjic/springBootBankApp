package com.example.BankApplication.transaction;

import com.example.BankApplication.pdfFile.StatementPdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository

public interface TransactionRepository extends JpaRepository<Transaction, StatementPdf> {

    @Query(value = "select * from dbtransaction order by createdat asc", nativeQuery = true)
    List<Transaction> findAllOrderByAsc();

    @Query(value = "select * from dbtransaction order by createdat desc", nativeQuery = true)
    List<Transaction> findAllOrderByDesc();

    @Query(value = "select * from dbtransaction where createdat between ?1 and ?2 order by createdat asc", nativeQuery = true)
    List<Transaction> findTransactionsByCreatedAtAsc(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query(value = "select * from dbtransaction where createdat between ?1 and ?2 order by createdat desc", nativeQuery = true)
    List<Transaction> findTransactionsByCreatedAtDesc(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("select t from Transaction t where t.userid = ?1")
    List<Transaction> getTransactionByUserId(Long userId);

    @Query("select t from Transaction t where t.id = ?1")
    Transaction getTransactionById(Long id);

    @Query("select SUM(t.amount) AS balance from Transaction t where t.sourceaccount = ?1")
    Double sourceAccountBalance(Long id);

    @Query("select SUM(t.amount) AS balance from Transaction t where t.destinationaccount = ?1")
    Double destinationAccountBalance(Long id);

    @Query(value = "select t.amount, t.id, u.firstname || ' ' || u.lastname AS sendername, t.createdat, " +
            "(select u.firstname || ' ' || u.lastname " +
            "from dbtransaction tr " +
            "inner join account acc on acc.id = t.destinationaccount " +
            "inner join dbuser userdb on userdb.id = acc.userid " +
            " where tr.id = t.id) AS receivername "+
            "from dbtransaction t " +
            "inner join account a on t.sourceaccount = a.id " +
            "inner join dbuser u on u.id = a.userid " +
            " where a.id = ?1", nativeQuery = true)
    List<Transaction> getStatement(Long accountId);

}
