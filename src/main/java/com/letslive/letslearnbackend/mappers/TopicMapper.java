package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.TopicDTO;
import com.letslive.letslearnbackend.entities.Topic;

public class TopicMapper {
    public static Topic toEntity(TopicDTO topicDTO) {
        Topic.TopicBuilder builder = Topic
                .builder()
                .id(topicDTO.getId())
                .sectionId(topicDTO.getSectionId())
                .title(topicDTO.getTitle())
                .studentCount(topicDTO.getStudentCount())
                .type(topicDTO.getType());

        return builder.build();
    }

    public static TopicDTO toDTO(Topic topic) {
        TopicDTO.TopicDTOBuilder builder = TopicDTO
                .builder()
                .id(topic.getId())
                .sectionId(topic.getSectionId())
                .title(topic.getTitle())
                .studentCount(topic.getStudentCount())
                .type(topic.getType());

        return builder.build();
    }
}
