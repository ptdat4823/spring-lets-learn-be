package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.CommentDTO;
import com.letslive.letslearnbackend.entities.Comment;

public class CommentMapper {
    public static CommentDTO toDTO(Comment comment) {
        return CommentDTO
                .builder()
                .id(comment.getId())
                .user(UserMapper.mapToDTO(comment.getUser()))
                .topic(TopicMapper.toDTO(comment.getTopic()))
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
