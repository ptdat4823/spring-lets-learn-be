package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.TeacherAssignmentResponseDTO;
import com.letslive.letslearnbackend.entities.TeacherAssignmentResponse;

public class TeacherAssignmentResponseMapper {
    public static TeacherAssignmentResponseDTO toDTO(TeacherAssignmentResponse teacherAssignmentResponse) {
        TeacherAssignmentResponseDTO.TeacherAssignmentResponseDTOBuilder builder = TeacherAssignmentResponseDTO.builder();
        builder
                .id(teacherAssignmentResponse.getId())
                .originalAssignmentResponseId(teacherAssignmentResponse.getOriginalAssignmentResponseId())
                .note(teacherAssignmentResponse.getNote())
                .mark(teacherAssignmentResponse.getMark())
                .gradedAt(teacherAssignmentResponse.getGradedAt())
                .gradedBy(UserMapper.mapToDTO(teacherAssignmentResponse.getGradedBy()));
        return builder.build();
    }

    public static TeacherAssignmentResponse toEntity(TeacherAssignmentResponseDTO teacherAssignmentResponseDTO) {
        TeacherAssignmentResponse.TeacherAssignmentResponseBuilder builder = TeacherAssignmentResponse.builder();
        builder
                .id(teacherAssignmentResponseDTO.getId())
                .originalAssignmentResponseId(teacherAssignmentResponseDTO.getOriginalAssignmentResponseId())
                .note(teacherAssignmentResponseDTO.getNote())
                .mark(teacherAssignmentResponseDTO.getMark())
                .gradedAt(teacherAssignmentResponseDTO.getGradedAt())
                .gradedBy(UserMapper.mapToEntity(teacherAssignmentResponseDTO.getGradedBy()));
        return builder.build();
    }
}
