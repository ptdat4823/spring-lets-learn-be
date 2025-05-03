package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.*;
import com.letslive.letslearnbackend.repositories.EnrollmentDetailRepository;
import com.letslive.letslearnbackend.security.JwtTokenVo;
import com.letslive.letslearnbackend.security.SecurityUtils;
import com.letslive.letslearnbackend.services.AuthService;
import com.letslive.letslearnbackend.services.UserService;
import com.letslive.letslearnbackend.utils.TimeUtils;
import jakarta.transaction.Transactional;
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
    private final AuthService authService;
    private final EnrollmentDetailRepository enrollmentDetailRepository;

    @GetMapping("/work")
    public ResponseEntity<List<TopicDTO>> getAllWorksOfUser(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) UUID courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start != null) start = TimeUtils.convertDateToGMT7Date(start);
        if (end != null) end = TimeUtils.convertDateToGMT7Date(end);
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.ok(userService.getAllWorksOfUser(vo.getUserID(), type, start, end));
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getSelfInformation() {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDTO user = userService.findUserById(vo.getUserID());
        return ResponseEntity.ok(user);
    }

    @PutMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> updateUserInformation(@RequestBody UpdateUserDTO updateUserDTO) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDTO user = userService.updateUserById(updateUserDTO, vo.getUserID());
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

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        authService.updatePassword(updatePasswordDTO, vo.getUserID());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/report")
    public ResponseEntity<StudentReportDTO> getLearnerReport(
            @RequestParam(required = false) UUID courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        if (start != null) start = TimeUtils.convertDateToGMT7Date(start);
        if (end != null) end = TimeUtils.convertDateToGMT7Date(end);
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.ok(userService.getStudentReport(vo.getUserID(), courseId, start, end));
    }

    @GetMapping ("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers () {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.ok(userService.getAllUsers(vo.getUserID()));
    }

    @DeleteMapping("/leave")
    @Transactional
    public ResponseEntity<Void> leaveCourse(@RequestParam UUID courseId) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        enrollmentDetailRepository.deleteByStudentIdAndCourseId(vo.getUserID(), courseId);
        return ResponseEntity.noContent().build();
    }
}
