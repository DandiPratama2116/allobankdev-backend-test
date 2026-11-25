package com.allobank.splitbill.repository;

import com.allobank.splitbill.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    List<Participant> findByBillGroupId(UUID billGroupId);
    Optional<Participant> findByBillGroupIdAndNameIgnoreCase(UUID billGroupId, String name);
    boolean existsByBillGroupIdAndNameIgnoreCase(UUID billGroupId, String name);
}
