package com.example.BankApplication.account;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "account")

public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String name;
    private Double initialbalance;
    private Long userid;
    private Date createdat;

    public Account() {
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
