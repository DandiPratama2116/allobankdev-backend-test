package com.allobank.splitbill.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "participants", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"bill_group_id", "name"})
})
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_group_id", nullable = false)
    private BillGroup billGroup;

    @Column(nullable = false)
    private String name;

    @Column
    private String email;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Participant() {
    }

    public Participant(String name, String email) {
        this.name = name;
        this.email = email;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BillGroup getBillGroup() {
        return billGroup;
    }

    public void setBillGroup(BillGroup billGroup) {
        this.billGroup = billGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
