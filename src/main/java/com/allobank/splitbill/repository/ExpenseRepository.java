package com.allobank.splitbill.repository;

import com.allobank.splitbill.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    
    @Query("SELECT DISTINCT e FROM Expense e " +
           "LEFT JOIN FETCH e.paidBy " +
           "LEFT JOIN FETCH e.splits s " +
           "LEFT JOIN FETCH s.participant " +
           "WHERE e.billGroup.id = :groupId " +
           "ORDER BY e.createdAt DESC")
    List<Expense> findByBillGroupIdWithDetails(@Param("groupId") UUID groupId);
}
