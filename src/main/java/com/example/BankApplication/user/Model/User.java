package com.example.BankApplication.user.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dbuser")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private String address;
    @Column(name = "phonenumber")
    private String phoneNumber;
    private String email;
    @Column(name = "createdat")
    private LocalDateTime createdAt;
    private String password;
    @Column(name = "verifyemail")
    private boolean verifyEmail;

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
}
