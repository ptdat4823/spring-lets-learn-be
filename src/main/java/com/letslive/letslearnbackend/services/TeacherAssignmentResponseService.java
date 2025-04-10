package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.SectionDTO;
import com.letslive.letslearnbackend.dto.TeacherAssignmentResponseDTO;
import com.letslive.letslearnbackend.dto.TopicDTO;
import com.letslive.letslearnbackend.entities.Section;
import com.letslive.letslearnbackend.entities.TeacherAssignmentResponse;
import com.letslive.letslearnbackend.entities.Topic;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.SectionMapper;
import com.letslive.letslearnbackend.mappers.TeacherAssignmentResponseMapper;
import com.letslive.letslearnbackend.repositories.TeacherAssignmentResponseRepository;
import com.letslive.letslearnbackend.repositories.TopicAssigmentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TeacherAssignmentResponseService {
    private final TeacherAssignmentResponseRepository teacherAssignmentResponseRepository;
    private final TopicAssigmentRepository topicAssigmentRepository;

    public TeacherAssignmentResponseDTO create(TeacherAssignmentResponseDTO dto) {
        TeacherAssignmentResponse response = teacherAssignmentResponseRepository.save(TeacherAssignmentResponseMapper.toEntity(dto));
        return TeacherAssignmentResponseMapper.toDTO(response);
    }

    public TeacherAssignmentResponseDTO getById(UUID responseId) {
        TeacherAssignmentResponse response = teacherAssignmentResponseRepository
                .findById(responseId)
                .orElseThrow(() -> new CustomException("Teacher assignment response not found!", HttpStatus.NOT_FOUND));

        return TeacherAssignmentResponseMapper.toDTO(response);
    }

    public TeacherAssignmentResponseDTO getByOriginalAssignmentId(UUID originalAssignmentId) {
        if (!topicAssigmentRepository.existsById(originalAssignmentId)) {
            throw new CustomException("Assignment response not found!", HttpStatus.NOT_FOUND);
        }

        TeacherAssignmentResponse response = teacherAssignmentResponseRepository
                .getByOriginalAssignmentResponseId(originalAssignmentId)
                .orElseThrow(() -> new CustomException("Teacher assignment response not found!", HttpStatus.NOT_FOUND));

        return TeacherAssignmentResponseMapper.toDTO(response);
    }

    public TeacherAssignmentResponseDTO updateById(UUID responseId, TeacherAssignmentResponseDTO dto) {
        if (!teacherAssignmentResponseRepository.existsById(responseId))
            throw new CustomException("Section not found!", HttpStatus.NOT_FOUND);

        TeacherAssignmentResponse updatedResponse = teacherAssignmentResponseRepository.save(TeacherAssignmentResponseMapper.toEntity(dto));
        return TeacherAssignmentResponseMapper.toDTO(updatedResponse);
    }
}
