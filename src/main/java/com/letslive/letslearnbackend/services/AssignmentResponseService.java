package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.AssignmentResponseDTO;
import com.letslive.letslearnbackend.entities.AssignmentResponse;
import com.letslive.letslearnbackend.entities.CloudinaryFile;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.AssignmentResponseMapper;
import com.letslive.letslearnbackend.repositories.AssignmentResponseRepository;
import com.letslive.letslearnbackend.repositories.CloudinaryFileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class AssignmentResponseService {
    private final AssignmentResponseRepository assignmentResponseRepository;
    private final CloudinaryFileRepository cloudinaryFileRepository;

    public AssignmentResponseDTO getAssignmentResponseById(UUID id) {
        AssignmentResponse res = assignmentResponseRepository.findById(id).orElseThrow(() -> new CustomException("Assignment response not found", HttpStatus.NOT_FOUND));
        return AssignmentResponseMapper.toDTO(res);
    }

    public AssignmentResponseDTO createAssignmentResponse(AssignmentResponseDTO quizResponseDTO) {
        AssignmentResponse assignmentResponse = AssignmentResponseMapper.toEntity(quizResponseDTO);

        List<CloudinaryFile> savedFiles = cloudinaryFileRepository.saveAll(assignmentResponse.getCloudinaryFiles());

        assignmentResponse.setCloudinaryFiles(null);
        AssignmentResponse firstCreated = assignmentResponseRepository.save(assignmentResponse);

        firstCreated.setCloudinaryFiles(savedFiles);
        AssignmentResponse savedAssignmentResponse = assignmentResponseRepository.save(firstCreated);

        return AssignmentResponseMapper.toDTO(savedAssignmentResponse);
    }

    public List<AssignmentResponseDTO> getAllAssignmentResponsesByTopicId(UUID topicId) {
        return assignmentResponseRepository.findAllByTopicId(topicId).stream().map(AssignmentResponseMapper::toDTO).toList();
    }

    public AssignmentResponseDTO updateAssignmentResponseById(UUID id, AssignmentResponseDTO quizResponseDTO) {
        if (!assignmentResponseRepository.existsById(id)) {
            throw new CustomException("Assignment response not found", HttpStatus.NOT_FOUND);
        }

        quizResponseDTO.setId(id);
        AssignmentResponse assignmentResponse = AssignmentResponseMapper.toEntity(quizResponseDTO);
        AssignmentResponse savedAssignmentResponse = assignmentResponseRepository.save(assignmentResponse);
        return AssignmentResponseMapper.toDTO(savedAssignmentResponse);
    }
}
