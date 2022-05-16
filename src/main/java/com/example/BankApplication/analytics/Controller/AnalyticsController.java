package com.example.BankApplication.analytics.Controller;

import com.example.BankApplication.analytics.Service.AnalyticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/analytics")

public class AnalyticsController {

    private final AnalyticsService analyticsService;
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Object> analyticsList(@PathVariable("id") Long id,
                                                @RequestParam(value = "order", required = false) String months){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(analyticsService.analytics(id, months));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
