package com.allobank.splitbill.service;

import com.allobank.splitbill.dto.AddParticipantRequest;
import com.allobank.splitbill.dto.CreateGroupRequest;
import com.allobank.splitbill.dto.GroupResponse;
import com.allobank.splitbill.dto.ParticipantResponse;
import com.allobank.splitbill.entity.BillGroup;
import com.allobank.splitbill.entity.Participant;
import com.allobank.splitbill.exception.InvalidOperationException;
import com.allobank.splitbill.exception.ResourceNotFoundException;
import com.allobank.splitbill.repository.BillGroupRepository;
import com.allobank.splitbill.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillGroupService {

    private final BillGroupRepository groupRepository;
    private final ParticipantRepository participantRepository;

    public BillGroupService(BillGroupRepository groupRepository, ParticipantRepository participantRepository) {
        this.groupRepository = groupRepository;
        this.participantRepository = participantRepository;
    }

    public GroupResponse createGroup(CreateGroupRequest request) {
        BillGroup group = new BillGroup(request.getName(), request.getDescription(), request.getCurrency());

        // Add participants
        List<String> names = request.getParticipants();
        for (String name : names) {
            if (name == null || name.isBlank()) {
                throw new InvalidOperationException("Participant name cannot be blank");
            }
            String trimmedName = name.trim();
            // Check for duplicates in creation request
            boolean existsInList = group.getParticipants().stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(trimmedName));
            if (existsInList) {
                throw new InvalidOperationException("Duplicate participant name in request: " + trimmedName);
            }

            Participant participant = new Participant(trimmedName, null);
            group.addParticipant(participant);
        }

        BillGroup saved = groupRepository.save(group);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroupById(UUID id) {
        BillGroup group = groupRepository.findByIdWithParticipants(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
        return mapToResponse(group);
    }

    @Transactional(readOnly = true)
    public BillGroup getGroupEntity(UUID id) {
        return groupRepository.findByIdWithParticipants(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
    }

    public ParticipantResponse addParticipant(UUID groupId, AddParticipantRequest request) {
        BillGroup group = getGroupEntity(groupId);

        String trimmedName = request.getName().trim();
        boolean exists = group.getParticipants().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(trimmedName));
        if (exists) {
            throw new InvalidOperationException("Participant with name '" + trimmedName + "' already exists in this group");
        }

        Participant participant = new Participant(trimmedName, request.getEmail());
        group.addParticipant(participant);
        Participant saved = participantRepository.save(participant);

        return new ParticipantResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getCreatedAt());
    }

    private GroupResponse mapToResponse(BillGroup group) {
        List<ParticipantResponse> participantResponses = group.getParticipants().stream()
                .map(p -> new ParticipantResponse(p.getId(), p.getName(), p.getEmail(), p.getCreatedAt()))
                .collect(Collectors.toList());

        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCurrency(),
                participantResponses,
                group.getExpenses().size(),
                group.getCreatedAt(),
                group.getUpdatedAt()
        );
    }
}
