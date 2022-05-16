package com.example.BankApplication.analytics.Database;

import com.example.BankApplication.analytics.Model.AnalyticsDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsDayRepository extends JpaRepository<AnalyticsDay, Long> {

    @Query(value = "select DATE_TRUNC('day', createdat) AS date, SUM(amount) AS total " +
            "FROM dbtransaction " +
            "WHERE sourceaccount = ?1 " +
            "GROUP BY DATE_TRUNC('day', createdat) ", nativeQuery = true)
    List<AnalyticsDay> analyticsForDay(Long id);
}
