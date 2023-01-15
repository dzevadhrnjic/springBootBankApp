package com.example.BankApplication.verification.Model;

import javax.persistence.*;

@Entity
@Table
public class Verification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String code;
    public Verification() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Verification{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
