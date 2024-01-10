package com.example.BankApplication.account.Model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dbaccount")

public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "initialbalance")
    private Double initialBalance;
    private Long userid;
    @Column(name = "createdat")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Double initialbalance) {
        this.initialBalance = initialbalance;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdat) {
        this.createdAt = createdat;
    }

}
