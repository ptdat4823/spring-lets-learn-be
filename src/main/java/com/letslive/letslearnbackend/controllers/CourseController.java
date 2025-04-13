package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.CourseDTO;
import com.letslive.letslearnbackend.dto.UserDTO;
import com.letslive.letslearnbackend.mappers.UserMapper;
import com.letslive.letslearnbackend.security.JwtTokenVo;
import com.letslive.letslearnbackend.security.SecurityUtils;
import com.letslive.letslearnbackend.services.CourseService;
import com.letslive.letslearnbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final UserService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CourseDTO>> getCoursesByUserID(@RequestParam(required = false) UUID userId) {
        if (userId != null) {
            List<CourseDTO> courses = courseService.getAllCoursesByUserID(userId);
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } else {
            courseService.getAllPublicCourses();
            List<CourseDTO> courses = courseService.getAllPublicCourses();
            return new ResponseEntity<>(courses, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseDTO> getCourse(@PathVariable UUID id) {
        CourseDTO course = courseService.getCourse(id);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDTO user = userService.findUserById(vo.getUserID());
        CourseDTO createdCourse = courseService.createCourse(UserMapper.mapToEntity(user), courseDTO);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable UUID id, @RequestBody CourseDTO courseDTO) {
        CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/join")
    public ResponseEntity<Void> joinCourse(@PathVariable UUID id) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDTO user = userService.findUserById(vo.getUserID());
        courseService.addUserToCourse(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
