package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.AssignmentResponseDTO;
import com.letslive.letslearnbackend.dto.UserDTO;
import com.letslive.letslearnbackend.security.JwtTokenVo;
import com.letslive.letslearnbackend.security.SecurityUtils;
import com.letslive.letslearnbackend.services.AssignmentResponseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/topic/{topicId}/assignment-response")
public class AssignmentResponseController {
    private final AssignmentResponseService assignmentResponseService;

    @PostMapping()
    public ResponseEntity<AssignmentResponseDTO> createAssignmentResponse(@RequestBody AssignmentResponseDTO dto, @PathVariable UUID topicId) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDTO createdAndModifiedUser = new UserDTO();
        createdAndModifiedUser.setId(vo.getUserID());
        dto.setStudent(createdAndModifiedUser);
        dto.setTopicId(topicId);

        AssignmentResponseDTO res = assignmentResponseService.createAssignmentResponse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping()
    public ResponseEntity<List<AssignmentResponseDTO>> getAllAssignmentResponsesByTopicId(@PathVariable UUID topicId) {
        return ResponseEntity.ok(assignmentResponseService.getAllAssignmentResponsesByTopicId(topicId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponseDTO> getAssignmentResponseById(@PathVariable("id") UUID resId) {
        return ResponseEntity.ok(assignmentResponseService.getAssignmentResponseById(resId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentResponseDTO> updateAssignmentResponseById(@PathVariable("id") UUID resId, @PathVariable("topicId") UUID topicId, @RequestBody AssignmentResponseDTO assignmentResponseDTO) {
        assignmentResponseDTO.setTopicId(topicId);
        return ResponseEntity.ok(assignmentResponseService.updateAssignmentResponseById(resId, assignmentResponseDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignmentResponseById(@PathVariable("id") UUID resId) {
        assignmentResponseService.deleteResponse(resId);
        return ResponseEntity.noContent().build();
    }
}