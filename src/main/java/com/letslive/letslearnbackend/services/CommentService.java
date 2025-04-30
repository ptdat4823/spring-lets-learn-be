package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.CommentDTO;
import com.letslive.letslearnbackend.entities.Comment;
import com.letslive.letslearnbackend.entities.Topic;
import com.letslive.letslearnbackend.entities.User;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.CommentMapper;
import com.letslive.letslearnbackend.repositories.CommentRepository;
import com.letslive.letslearnbackend.repositories.TopicRepository;
import com.letslive.letslearnbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    public CommentDTO addComment(UUID commenterId, UUID topicId, CommentDTO commentDTO) {
        User commenter = userRepository.findById(commenterId).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new CustomException("Topic not found", HttpStatus.NOT_FOUND));

        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setUser(commenter);
        comment.setTopic(topic);
        Comment createdComment = commentRepository.save(comment);
        return CommentMapper.toDTO(createdComment);
    }

    public List<CommentDTO> getCommentsByTopic(UUID topicId) {
        return commentRepository.findByTopicId(topicId).stream().map(CommentMapper::toDTO).toList();
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}