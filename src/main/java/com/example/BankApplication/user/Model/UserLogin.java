package com.example.BankApplication.user.Model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dbuser")
@Data
public class UserLogin {

    private String email;
    private String password;
    @Id
    private Long id;
}
