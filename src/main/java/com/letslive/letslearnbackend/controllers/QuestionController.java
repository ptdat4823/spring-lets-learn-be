package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.QuestionDTO;
import com.letslive.letslearnbackend.dto.UserDTO;
import com.letslive.letslearnbackend.entities.Question;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.QuestionMapper;
import com.letslive.letslearnbackend.security.JwtTokenVo;
import com.letslive.letslearnbackend.security.SecurityUtils;
import com.letslive.letslearnbackend.services.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/question")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping("")
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody QuestionDTO questionDTO, HttpServletRequest request) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDTO createdAndModifiedUser = new UserDTO();
        createdAndModifiedUser.setId(vo.getUserID());
        questionDTO.setCreatedBy(createdAndModifiedUser);
        questionDTO.setModifiedBy(createdAndModifiedUser);

        QuestionDTO createdQuestion = questionService.createQuestion(questionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }

    @GetMapping()
    public ResponseEntity<List<QuestionDTO>> getAllQuestionsByCourseId(@RequestParam UUID courseId) {
        return ResponseEntity.ok(questionService.getAllQuestionsByCourseId(courseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable("id") UUID questionId) {
        return ResponseEntity.ok(questionService.getQuestionById(questionId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable("id") UUID questionId, QuestionDTO questionDTO) {
        return ResponseEntity.ok(questionService.updateQuestion(questionId, questionDTO));
    }
}