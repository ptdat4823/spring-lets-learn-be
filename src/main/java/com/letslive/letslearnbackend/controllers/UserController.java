package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.*;
import com.letslive.letslearnbackend.security.JwtTokenVo;
import com.letslive.letslearnbackend.security.SecurityUtils;
import com.letslive.letslearnbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/work")
    public ResponseEntity<List<StudentWorksInACourseDTO>> getAllWorksOfUser(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) UUID courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.ok(userService.getAllWorksOfUser(vo.getUserID(), type, courseId, start, end));
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getSelfInformation() {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDTO user = userService.findUserById(vo.getUserID());
        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUserInformation(@PathVariable UUID id) {
        UserDTO user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/assignment-responses")
    public ResponseEntity<List<AssignmentResponseDTO>> getAllAssignmentResponsesOfUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getAllAssignmentResponsesOfUser(userId));
    }

    @GetMapping("/{id}/quiz-responses")
    public ResponseEntity<List<QuizResponseDTO>> getAllQuizResponsesOfUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getAllQuizResponsesOfUser(userId));
    }
}
