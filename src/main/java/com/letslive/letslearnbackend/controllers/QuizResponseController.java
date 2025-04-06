package com.letslive.letslearnbackend.controllers;


import com.letslive.letslearnbackend.dto.QuestionDTO;
import com.letslive.letslearnbackend.dto.QuizResponseDTO;
import com.letslive.letslearnbackend.dto.UserDTO;
import com.letslive.letslearnbackend.entities.QuizResponse;
import com.letslive.letslearnbackend.security.JwtTokenVo;
import com.letslive.letslearnbackend.security.SecurityUtils;
import com.letslive.letslearnbackend.services.QuestionService;
import com.letslive.letslearnbackend.services.QuizResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/topic/{topicId}/quiz-response")
public class QuizResponseController {
    private final QuizResponseService quizResponseService;

    @PostMapping()
    public ResponseEntity<QuizResponseDTO> createQuizResponse(@RequestBody QuizResponseDTO dto, @PathVariable UUID topicId) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDTO createdAndModifiedUser = new UserDTO();
        createdAndModifiedUser.setId(vo.getUserID());
        dto.setStudent(createdAndModifiedUser);
        dto.setTopicId(topicId);

        QuizResponseDTO res = quizResponseService.createQuizResponse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping()
    public ResponseEntity<List<QuizResponseDTO>> getAllQuizResponsesByTopicId(@PathVariable UUID topicId) {
        return ResponseEntity.ok(quizResponseService.getAllQuizResponsesByTopicId(topicId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponseDTO> getQuizResponseById(@PathVariable("id") UUID resId) {
        return ResponseEntity.ok(quizResponseService.getQuizResponseById(resId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizResponseDTO> updateQuizResponseById(@PathVariable("id") UUID resId, @PathVariable("topicId") UUID topicId, @RequestBody QuizResponseDTO quizResponseDTO) {
        quizResponseDTO.setTopicId(topicId);
        return ResponseEntity.ok(quizResponseService.updateQuizResponseById(resId, quizResponseDTO));
    }
}
