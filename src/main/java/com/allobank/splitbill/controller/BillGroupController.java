package com.allobank.splitbill.controller;

import com.allobank.splitbill.dto.AddParticipantRequest;
import com.allobank.splitbill.dto.CreateGroupRequest;
import com.allobank.splitbill.dto.GroupResponse;
import com.allobank.splitbill.dto.ParticipantResponse;
import com.allobank.splitbill.service.BillGroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
public class BillGroupController {

    private final BillGroupService groupService;

    public BillGroupController(BillGroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        GroupResponse response = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @PostMapping("/{groupId}/participants")
    public ResponseEntity<ParticipantResponse> addParticipant(
            @PathVariable UUID groupId,
            @Valid @RequestBody AddParticipantRequest request) {
        ParticipantResponse response = groupService.addParticipant(groupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
