package com.example.BankApplication.account;

import java.util.Date;

public class Account {

    private Long id;
    private String name;
    private Double initialbalance;
    private Long userid;
    private Date createdat;
    private Double balance;

    public Account() {
    }

    public Account(Long id, String name, double initialbalance, Long userid, Date createdat) {
        this.id = id;
        this.name = name;
        this.initialbalance = initialbalance;
        this.userid = userid;
        this.createdat = createdat;
    }

    public Account(String name, double initialbalance, Long userid, Date createdat) {
        this.name = name;
        this.initialbalance = initialbalance;
        this.userid = userid;
        this.createdat = createdat;
    }

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

    public Double getInitialbalance() {
        return initialbalance;
    }

    public void setInitialbalance(Double initialbalance) {
        this.initialbalance = initialbalance;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Date getCreatedat() {
        return createdat;
    }

    public void setCreatedat(Date createdat) {
        this.createdat = createdat;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", initialbalance=" + initialbalance +
                ", userid=" + userid +
                ", createdat=" + createdat +
                '}';
    }
}
