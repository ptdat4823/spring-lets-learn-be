package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.AssignmentResponseDTO;
import com.letslive.letslearnbackend.dto.StudentWorksInACourseDTO;
import com.letslive.letslearnbackend.dto.TopicDTO;
import com.letslive.letslearnbackend.dto.UserDTO;
import com.letslive.letslearnbackend.security.JwtTokenVo;
import com.letslive.letslearnbackend.security.SecurityUtils;
import com.letslive.letslearnbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/work")
    public ResponseEntity<List<StudentWorksInACourseDTO>> getAllWorksOfUser(@RequestParam(required = false) String type, @RequestParam(required = false) UUID courseId) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.ok(userService.getAllWorksOfUser(vo.getUserID(), type, courseId));
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
    public ResponseEntity<List<AssignmentResponseDTO>> getAllAssignmentResponsesOfUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getAllAssignmentResponsesOfUser(id));
    }
}
