package com.example.BankApplication.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("select u from User u where u.id = ?1") //?1 znaci prvi param koji hoces da prosljedis, to ce bit user id
    User getUserById(Long id);

}
