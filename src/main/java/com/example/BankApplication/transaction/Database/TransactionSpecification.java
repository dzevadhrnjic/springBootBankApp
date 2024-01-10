package com.example.BankApplication.transaction.Database;

import com.example.BankApplication.transaction.Model.Transaction;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.ZoneId;

public class  TransactionSpecification {

    public static Specification<Transaction> orderByAscOrByDescCreatedat(String order) {
        return (root, query, criteriaBuilder) -> {

            if (order.equalsIgnoreCase("asc")) {
                query.orderBy(criteriaBuilder.asc(root.get("createdat")));
            } else if (order.equalsIgnoreCase("desc")){
                query.orderBy(criteriaBuilder.desc(root.get("createdat")));
            }
            return query.getRestriction();
        };
    }

    public static Specification<Transaction> transactionByDateAsc(String order, Date dateFrom , Date dateTo)  {
        return ((root, query, criteriaBuilder) -> {
            Predicate predicates = criteriaBuilder.conjunction();

            LocalDateTime fromDate = dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime toDate = dateTo.toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDateTime().withHour(23).withMinute(59).withSecond(59);

            predicates = criteriaBuilder.and(predicates, criteriaBuilder.greaterThanOrEqualTo(root.get("createdat"), fromDate));

            predicates = criteriaBuilder.and(predicates, criteriaBuilder.lessThanOrEqualTo(root.get("createdat"), toDate));

            if (order.equalsIgnoreCase("asc")) {
                query.orderBy(criteriaBuilder.asc(root.get("createdat")));
            }
            else if (order.equalsIgnoreCase("desc")){
                query.orderBy(criteriaBuilder.desc(root.get("createdat")));
            }
            query.where(predicates);

            return query.getRestriction();
        });
    }
}
