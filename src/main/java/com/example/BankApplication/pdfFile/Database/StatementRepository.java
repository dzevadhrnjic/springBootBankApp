package com.example.BankApplication.pdfFile.Database;

import com.example.BankApplication.pdfFile.Model.StatementPdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface StatementRepository extends JpaRepository<StatementPdf, Long> {

    @Query(value = "select dbtransaction.amount, dbtransaction.id, dbuser.firstname || ' ' || dbuser.lastname AS sendername," +
            " dbtransaction.createdat, dbuser .email, " +
            "(select userdb.firstname || ' ' || userdb.lastname " +
            "from dbtransaction tr " +
            "inner join account acc on acc.id = dbtransaction.destinationaccount " +
            "inner join dbuser userdb on userdb.id = acc.userid " +
            " where tr.id = dbtransaction.id) AS receivername "+
            "from dbtransaction " +
            "inner join account on dbtransaction.sourceaccount = account.id " +
            "inner join dbuser on dbuser.id = account.userid " +
            " where account.id = ?1 order by dbtransaction.createdat desc", nativeQuery = true)
    List<StatementPdf> getStatement(Long accountId);
}
