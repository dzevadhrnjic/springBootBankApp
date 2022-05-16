package com.example.BankApplication.analytics.Database;

import com.example.BankApplication.analytics.Model.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics,Long> {

    @Query(value = "select DATE_TRUNC('month', createdat) AS date, SUM(amount) AS total " +
            "FROM dbtransaction " +
            "WHERE sourceaccount = ?1 " +
            "GROUP BY DATE_TRUNC('month',createdat) ", nativeQuery = true)
    List<Analytics> analyticsForMonths(Long id);

}
