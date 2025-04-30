package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.CommentDTO;
import com.letslive.letslearnbackend.security.JwtTokenVo;
import com.letslive.letslearnbackend.security.SecurityUtils;
import com.letslive.letslearnbackend.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/course/{courseId}/topic/{topicId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDTO> addComment(@PathVariable(name = "topicId") UUID topicId, @RequestBody CommentDTO commentDTO) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.ok(commentService.addComment(vo.getUserID(), topicId, commentDTO));
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable(name = "topicId") UUID topicId) {
        List<CommentDTO> comments = commentService.getCommentsByTopic(topicId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}