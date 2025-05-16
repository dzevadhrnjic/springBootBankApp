package com.example.BankApplication.user.Database;

import com.example.BankApplication.user.Model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAll(Specification<User> specification, Pageable paging);

    @Query("select u from User u where u.id = ?1")
    User getUserById(Long id);

    @Query("select u from User u where u.email = ?1 and u.password = ?2")
    User getUserByEmailAndPassword(String email, String password);

    @Query("select u from User u where u.email = ?1")
    User getUserByEmail(String email);

}