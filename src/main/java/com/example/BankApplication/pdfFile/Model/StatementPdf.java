package com.example.BankApplication.pdfFile.Model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "dbtransaction")
@Data
public class StatementPdf {

    private String sendername;
    private String  receivername;
    private Double amount;
    private Date createdat;
    private String email;
    @Id
    private Long id;
}
