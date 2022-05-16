package com.example.BankApplication.analytics.Service;

import com.example.BankApplication.analytics.Database.AnalyticsDayRepository;
import com.example.BankApplication.analytics.Database.AnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    @Autowired
    AnalyticsRepository analyticsRepository;

    @Autowired
    AnalyticsDayRepository analyticsDayRepository;

    public List<? extends Object> analytics(Long id, String order){

        if (order.equals("months")) {
            return analyticsRepository.analyticsForMonths(id);
        }else {
            return analyticsDayRepository.analyticsForDay(id);
        }
    }
}
