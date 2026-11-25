package com.allobank.splitbill.repository;

import com.allobank.splitbill.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("SELECT p FROM Payment p " +
           "LEFT JOIN FETCH p.payer " +
           "LEFT JOIN FETCH p.payee " +
           "WHERE p.billGroup.id = :groupId " +
           "ORDER BY p.createdAt DESC")
    List<Payment> findByBillGroupIdWithDetails(@Param("groupId") UUID groupId);
}
