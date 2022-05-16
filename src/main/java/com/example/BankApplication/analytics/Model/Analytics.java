package com.example.BankApplication.analytics.Model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "dbtransaction")

public class Analytics {

    @Id
    @JsonFormat(pattern="yyyy-MM")
    private Date date;
    private Double total;

    public Analytics() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
