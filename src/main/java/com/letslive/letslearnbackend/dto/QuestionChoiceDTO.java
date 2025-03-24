package com.letslive.letslearnbackend.dto;

import com.letslive.letslearnbackend.entities.Question;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class QuestionChoiceDTO {
    private UUID id;
    private String text;
    private BigDecimal gradePercent;
    private String feedback;
    private UUID questionId;
}
