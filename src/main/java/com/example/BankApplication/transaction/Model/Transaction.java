package com.example.BankApplication.transaction.Model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dbtransaction")

public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sourceaccount;
    private Long destinationaccount;
    private Double amount;
    private LocalDateTime createdat;
    private Long userid;
    public Transaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceaccount() {
        return sourceaccount;
    }

    public void setSourceaccount(Long sourceaccount) {
        this.sourceaccount = sourceaccount;
    }

    public Long getDestinationaccount() {
        return destinationaccount;
    }

    public void setDestinationaccount(Long destinationaccount) {
        this.destinationaccount = destinationaccount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    //    public Date getCreatedat() {
//        return createdat;
//    }
//
//    public void setCreatedat(Date createdat) {
//        this.createdat = createdat;
//    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getUserid() {
        return userid;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sourceaccount=" + sourceaccount +
                ", destinationaccount=" + destinationaccount +
                ", amount=" + amount +
                ", createdat=" + createdat +
                ", userid=" + userid +
                '}';
    }
}
