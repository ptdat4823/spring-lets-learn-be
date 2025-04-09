package com.letslive.letslearnbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.letslive.letslearnbackend.dto.TopicDTO;
import com.letslive.letslearnbackend.entities.*;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.TopicMapper;
import com.letslive.letslearnbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicQuizRepository topicQuizRepository;
    private final TopicQuizQuestionRepository topicQuizQuestionRepository;
    private final TopicQuizQuestionChoiceRepository topicQuizQuestionChoiceRepository;
    private final TopicAssigmentRepository topicAssigmentRepository;
    private final TopicMeetingRepository topicMeetingRepository;

    ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    public TopicDTO createTopic(TopicDTO topicDTO) {
        Topic topic = TopicMapper.toEntity(topicDTO);
        Topic createdTopic = topicRepository.save(topic);

        // check if there is data, if not then just return
        if (topicDTO.getData() == null || topicDTO.getData().isEmpty()) {
            return TopicMapper.toDTO(createdTopic);
        }

        String createdTopicData;

        switch (topic.getType()) {
            case "quiz":
                try {
                    // save the topic quiz metadata
                    TopicQuiz topicQuiz = mapper.readValue(topicDTO.getData(), TopicQuiz.class);
                    topicQuiz.setTopicId(createdTopic.getId());
                    TopicQuiz createdTopicQuiz = topicQuizRepository.save(topicQuiz);

                    // save the questions and its choices
                    topicQuiz.getQuestions().forEach(question -> {
                        question.setId(null);
                        question.setTopicQuizId(topicQuiz.getId());
                        UUID createdQuestionId = topicQuizQuestionRepository.save(question).getId();

                        question.getChoices().forEach(c -> {
                            c.setQuestionId(createdQuestionId); // Use the ID of the parent question
                            topicQuizQuestionChoiceRepository.save(c);
                        });
                    });

                    TopicQuiz finalTopicQuizSaved = topicQuizRepository
                            .findById(createdTopicQuiz.getId())
                            .orElseThrow(() -> new CustomException("Something unexpected happened!", HttpStatus.INTERNAL_SERVER_ERROR));

                    createdTopicData = mapper.writeValueAsString(finalTopicQuizSaved);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing quiz data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "assignment":
                try {
                    TopicAssignment topicAssigment = mapper.readValue(topicDTO.getData(), TopicAssignment.class);
                    topicAssigment.setId(null);
                    topicAssigment.setTopicId(createdTopic.getId());
                    TopicAssignment createdTopicAssigment = topicAssigmentRepository.save(topicAssigment);
                    createdTopicData = mapper.writeValueAsString(createdTopicAssigment);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing assigment data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "meeting":
                try {
                    TopicMeeting topicMeeting = mapper.readValue(topicDTO.getData(), TopicMeeting.class);
                    topicMeeting.setId(null);
                    topicMeeting.setTopicId(createdTopic.getId());
                    TopicMeeting createdTopicMeeting = topicMeetingRepository.save(topicMeeting);
                    createdTopicData = mapper.writeValueAsString(createdTopicMeeting);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing meeting data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
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
        if (!topicRepository.existsById(topicDTO.getId())) {
            throw new CustomException("No topic found!", HttpStatus.NOT_FOUND);
        }
        Topic updatedTopic = topicRepository.save(TopicMapper.toEntity(topicDTO));

        // check if there is data, if not then just return
        if (topicDTO.getData() == null || topicDTO.getData().isEmpty()) {
            return TopicMapper.toDTO(updatedTopic);
        }

        TopicDTO updatedTopicDTO = TopicMapper.toDTO(updatedTopic);

        switch (topicDTO.getType().toLowerCase()) {
            case "quiz":
                try {
                    // update the topic quiz metadata
                    TopicQuiz topicQuiz = mapper.readValue(topicDTO.getData(), TopicQuiz.class);
                    topicQuizRepository.findById(topicQuiz.getId()).orElseThrow(() -> new CustomException("No topic quiz found!", HttpStatus.NOT_FOUND));
                    topicQuizRepository.save(topicQuiz);

                    // remove every questions and choices in topic quiz
                    List<TopicQuizQuestion> topicQuizQuestions = topicQuizQuestionRepository.findAllByTopicQuizId(topicQuiz.getId());
                    topicQuizQuestions.forEach(topicQuizQuestion -> {
                        topicQuizQuestion.getChoices().forEach(choice -> {
                            topicQuizQuestionChoiceRepository.deleteById(choice.getId());
                        });

                        topicQuizQuestionRepository.deleteById(topicQuizQuestion.getId());
                    });

                    // save the questions and its choices
                    topicQuiz.getQuestions().forEach(question -> {
                        question.setId(null);
                        question.setTopicQuizId(topicQuiz.getId());
                        UUID createdQuestionId = topicQuizQuestionRepository.save(question).getId();

                        question.getChoices().forEach(c -> {
                            c.setId(null); // remove "set id", generate a new id please
                            c.setQuestionId(createdQuestionId); // Use the ID of the parent question
                            topicQuizQuestionChoiceRepository.save(c);
                        });
                    });

                    // BUG: RETURNING OLD DATA
                    topicQuizRepository.flush();
                    TopicQuiz updatedTopicQuizData = topicQuizRepository.findById(topicQuiz.getId()).orElseThrow(() -> new CustomException("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR));
                    updatedTopicDTO.setData(mapper.writeValueAsString(updatedTopicQuizData));
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing quiz data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                } catch (Exception e) {
                    throw new CustomException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            case "assignment":
                try {
                    TopicAssignment topicAssigment = mapper.readValue(topicDTO.getData(), TopicAssignment.class);
                    if (!topicAssigmentRepository.existsById(topicAssigment.getId())) {
                        throw new CustomException("Assigment not found!", HttpStatus.NOT_FOUND);
                    }
                    TopicAssignment createdTopicAssigment = topicAssigmentRepository.save(topicAssigment);
                    updatedTopicDTO.setData(mapper.writeValueAsString(createdTopicAssigment));
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing assigment data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }

                break;
            case "meeting":
                try {
                    TopicMeeting topicMeeting = mapper.readValue(topicDTO.getData(), TopicMeeting.class);
                    if (!topicMeetingRepository.existsById(topicMeeting.getId())) {
                        throw new CustomException("Assigment not found!", HttpStatus.NOT_FOUND);
                    }
                    TopicMeeting updatedTopicMeeting = topicMeetingRepository.save(topicMeeting);
                    updatedTopicDTO.setData(mapper.writeValueAsString(updatedTopicMeeting));
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing meeting data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            default:
                throw new CustomException("Topic type not found!", HttpStatus.BAD_REQUEST);
        }

        return updatedTopicDTO;
    }

    public void deleteTopic(UUID id) {
        topicRepository.deleteById(id);
    }

    public TopicDTO getTopicById(UUID id) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new CustomException("No topic found!", HttpStatus.NOT_FOUND));
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
            case "assignment":
                TopicAssignment topicAssignment = topicAssigmentRepository.findByTopicId(topic.getId());
                try {
                    topicData = mapper.writeValueAsString(topicAssignment);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing assigment data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "meeting":
                TopicMeeting topicMeeting = topicMeetingRepository.findById(topic.getId()).orElseThrow(() -> new CustomException("No topic meeting found!", HttpStatus.NOT_FOUND));
                try {
                    topicData = mapper.writeValueAsString(topicMeeting);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing meeting data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
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
