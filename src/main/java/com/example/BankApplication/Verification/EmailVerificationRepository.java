package com.example.BankApplication.Verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository

public interface EmailVerificationRepository extends JpaRepository<Verification, Long> {

  @Query("select v from Verification v where v.email = ?1 and v.code = ?2")
  Verification getEmailAndCode(String email, String code);

}
