package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.TopicDTO;
import com.letslive.letslearnbackend.entities.Topic;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.TopicMapper;
import com.letslive.letslearnbackend.repositories.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;

    public TopicDTO createTopic(TopicDTO topicDTO) {
        Topic topic = TopicMapper.toEntity(topicDTO);
        topicRepository.save(topic);
        return TopicMapper.toDTO(topic);
    }

    public TopicDTO updateTopic(TopicDTO topicDTO) {
        Topic topic = topicRepository.findById(topicDTO.getId()).orElseThrow(() -> new CustomException("No topic found!", HttpStatus.NOT_FOUND));
        Topic updatedTopic = topicRepository.save(topic);
        return TopicMapper.toDTO(updatedTopic);
    }

    public void deleteTopic(UUID id) {
        topicRepository.deleteById(id);
    }

    public TopicDTO getTopicById(UUID id) {
        return TopicMapper.toDTO(topicRepository.findById(id).orElseThrow(() -> new CustomException("No topic found!", HttpStatus.NOT_FOUND)));
    }
}
