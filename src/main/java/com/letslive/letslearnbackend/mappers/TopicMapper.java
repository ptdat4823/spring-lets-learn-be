package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.TopicDTO;
import com.letslive.letslearnbackend.entities.Topic;

public class TopicMapper {
    public static Topic toEntity(TopicDTO topicDTO) {
        Topic.TopicBuilder builder = Topic
                .builder()
                .sectionId(topicDTO.getSectionId())
                .title(topicDTO.getTitle())
                .type(topicDTO.getType());

        if (topicDTO.getAttachedFile() != null && !topicDTO.getAttachedFile().isBlank()) {
            builder.attachedFile(topicDTO.getAttachedFile());
        }

        return builder.build();
    }

    public static TopicDTO toDTO(Topic topic) {
        TopicDTO.TopicDTOBuilder builder = TopicDTO
                .builder()
                .id(topic.getId())
                .sectionId(topic.getSectionId())
                .title(topic.getTitle())
                .type(topic.getType())
                .attachedFile(topic.getAttachedFile());

        return builder.build();
    }
}
