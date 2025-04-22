package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.SingleQuizReportDTO;
import com.letslive.letslearnbackend.dto.TopicDTO;
import com.letslive.letslearnbackend.security.JwtTokenVo;
import com.letslive.letslearnbackend.security.SecurityUtils;
import com.letslive.letslearnbackend.services.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/topic")
@Validated
public class TopicController {
    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping
    public ResponseEntity<TopicDTO> createTopic(@RequestBody TopicDTO topicDTO) {
        TopicDTO createdTopic = topicService.createTopic(topicDTO);
        return ResponseEntity.status(201).body(createdTopic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicDTO> updateTopic(
            @PathVariable UUID id,
            @RequestBody TopicDTO topicDTO
    ) {
        topicDTO.setId(id); // Ensure the ID matches the path variable
        TopicDTO updatedTopic = topicService.updateTopic(topicDTO);
        return ResponseEntity.ok(updatedTopic);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable UUID id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicDTO> getTopicById(@PathVariable UUID id) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        TopicDTO topicDTO = topicService.getTopicById(id, vo.getUserID());
        return ResponseEntity.ok(topicDTO);
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<SingleQuizReportDTO> getTopicById(@PathVariable UUID id, @RequestParam UUID courseId) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.ok(topicService.getSingleQuizReport(id, vo.getUserID()));
    }
}