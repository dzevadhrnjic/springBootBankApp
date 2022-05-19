package com.example.BankApplication.user.Database;

import com.example.BankApplication.user.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("select u from User u where u.id = ?1")
    User getUserById(Long id);

    @Query("select u from User u where u.email = ?1 and u.password = ?2")
    User getUser(String email, String password);

    @Query("select u from User u where u.email = ?1")
    User getUserByEmail(String email);

}
