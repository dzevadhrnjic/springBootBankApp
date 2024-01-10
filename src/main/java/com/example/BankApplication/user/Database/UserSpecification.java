package com.example.BankApplication.user.Database;

import com.example.BankApplication.user.Model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasFirstName(String firstname) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("firstname"), firstname));
    }

    public static Specification<User> hasLastName(String lastname) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("lastname"), lastname));
    }

    public static Specification<User> hasAddress(String address) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("address"), address));
    }

    public static Specification<User> hasEmail(String email) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("email"), email));
    }
}
