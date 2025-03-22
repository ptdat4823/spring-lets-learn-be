package com.letslive.letslearnbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.letslive.letslearnbackend.dto.TopicDTO;
import com.letslive.letslearnbackend.entities.Topic;
import com.letslive.letslearnbackend.entities.TopicAssigment;
import com.letslive.letslearnbackend.entities.TopicQuiz;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.TopicMapper;
import com.letslive.letslearnbackend.repositories.TopicAssigmentRepository;
import com.letslive.letslearnbackend.repositories.TopicQuizRepository;
import com.letslive.letslearnbackend.repositories.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicQuizRepository topicQuizRepository;
    private final TopicAssigmentRepository topicAssigmentRepository;

    public TopicDTO createTopic(TopicDTO topicDTO) {
        Topic topic = TopicMapper.toEntity(topicDTO);
        Topic createdTopic = topicRepository.save(topic);
        String createdTopicData;
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        switch (topic.getType()) {
            case "quiz":
                try {
                    TopicQuiz topicQuiz = mapper.readValue(topicDTO.getData(), TopicQuiz.class);
                    topicQuiz.setTopicId(createdTopic.getId());
                    TopicQuiz createdTopicQuiz = topicQuizRepository.save(topicQuiz);
                    createdTopicData = mapper.writeValueAsString(createdTopicQuiz);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing quiz data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "assigment":
                try {
                    TopicAssigment topicAssigment = mapper.readValue(topicDTO.getData(), TopicAssigment.class);
                    topicAssigment.setTopicId(createdTopic.getId());
                    TopicAssigment createdTopicAssigment = topicAssigmentRepository.save(topicAssigment);
                    createdTopicData = mapper.writeValueAsString(createdTopicAssigment);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing assigment data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            default:
                throw new CustomException("Topic type not found!", HttpStatus.BAD_REQUEST);
        }

        TopicDTO createdTopicDTO = TopicMapper.toDTO(createdTopic);
        createdTopicDTO.setData(createdTopicData);

        return createdTopicDTO;
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
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new CustomException("No topic found!", HttpStatus.NOT_FOUND));
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        String topicData;

        switch (topic.getType()) {
            case "quiz":
                TopicQuiz topicQuiz = topicQuizRepository.findByTopicId(topic.getId());
                try {
                    topicData = mapper.writeValueAsString(topicQuiz);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing quiz data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "assigment":
                TopicAssigment topicAssigment = topicAssigmentRepository.findByTopicId(topic.getId());
                try {
                    topicData = mapper.writeValueAsString(topicAssigment);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing assigment data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            default:
                throw new CustomException("Topic type not found!", HttpStatus.BAD_REQUEST);
        }

        TopicDTO createdTopicDTO = TopicMapper.toDTO(topic);
        createdTopicDTO.setData(topicData);

        return createdTopicDTO;
    }
}
