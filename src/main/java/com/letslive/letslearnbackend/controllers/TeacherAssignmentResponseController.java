package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.TeacherAssignmentResponseDTO;
import com.letslive.letslearnbackend.services.TeacherAssignmentResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/teacher-assignment-responses")
@RequiredArgsConstructor
public class TeacherAssignmentResponseController {
    private final TeacherAssignmentResponseService teacherAssignmentResponseService;

    // Create a new teacher assignment response
    @PostMapping
    public ResponseEntity<TeacherAssignmentResponseDTO> create(@RequestBody TeacherAssignmentResponseDTO dto) {
        TeacherAssignmentResponseDTO createdResponse = teacherAssignmentResponseService.create(dto);
        return ResponseEntity.status(201).body(createdResponse);
    }

    // Get a teacher assignment response by ID
    @GetMapping("/{responseId}")
    public ResponseEntity<TeacherAssignmentResponseDTO> getById(@PathVariable UUID responseId) {
        TeacherAssignmentResponseDTO response = teacherAssignmentResponseService.getById(responseId);
        return ResponseEntity.ok(response);
    }

    // Get a teacher assignment response by original assignment ID
    @GetMapping("/original-assignment/{originalAssignmentId}")
    public ResponseEntity<TeacherAssignmentResponseDTO> getByOriginalAssignmentId(@PathVariable UUID originalAssignmentId) {
        TeacherAssignmentResponseDTO response = teacherAssignmentResponseService.getByOriginalAssignmentId(originalAssignmentId);
        return ResponseEntity.ok(response);
    }

    // Update a teacher assignment response by ID
    @PutMapping("/{responseId}")
    public ResponseEntity<TeacherAssignmentResponseDTO> updateById(@PathVariable UUID responseId, @RequestBody TeacherAssignmentResponseDTO dto) {
        TeacherAssignmentResponseDTO updatedResponse = teacherAssignmentResponseService.updateById(responseId, dto);
        return ResponseEntity.ok(updatedResponse);
    }
}