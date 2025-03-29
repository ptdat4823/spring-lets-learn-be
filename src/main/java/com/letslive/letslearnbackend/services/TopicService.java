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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicQuizRepository topicQuizRepository;
    private final TopicQuizQuestionRepository topicQuizQuestionRepository;
    private final TopicQuizQuestionChoiceRepository topicQuizQuestionChoiceRepository;
    private final TopicAssigmentRepository topicAssigmentRepository;

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
                        question.setTopicQuizId(topicQuiz.getId());
                        UUID createdQuestionId = topicQuizQuestionRepository.save(question).getId();

                        question.getChoices().forEach(c -> {
                            c.setQuestionId(createdQuestionId); // Use the ID of the parent question
                            topicQuizQuestionChoiceRepository.save(c);
                        });
                    });

                    TopicQuiz finalTopicQuizSaved = topicQuizRepository.getById(createdTopicQuiz.getId());

                    createdTopicData = mapper.writeValueAsString(finalTopicQuizSaved);
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
        topicRepository.findById(topicDTO.getId()).orElseThrow(() -> new CustomException("No topic found!", HttpStatus.NOT_FOUND));
        Topic updatedTopic = topicRepository.save(TopicMapper.toEntity(topicDTO));

        // check if there is data, if not then just return
        if (topicDTO.getData() == null || topicDTO.getData().isEmpty()) {
            return TopicMapper.toDTO(updatedTopic);
        }
        String updatedTopicData;

        switch (topicDTO.getType().toLowerCase()) {
            case "quiz":
                try {
                    // update the topic quiz metadata
                    TopicQuiz topicQuiz = mapper.readValue(topicDTO.getData(), TopicQuiz.class);
                    topicQuizRepository.findById(topicQuiz.getId()).orElseThrow(() -> new CustomException("No topic quiz found!", HttpStatus.NOT_FOUND));
                    topicQuizRepository.save(topicQuiz);

                    // remove every questions and choices in topic quiz
                    topicQuizQuestionRepository.findAllByTopicQuizId(topicQuiz.getId()).forEach(topicQuizQuestion -> {
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
                    updatedTopicData = mapper.writeValueAsString(updatedTopicQuizData);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing quiz data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                } catch (Exception e) {
                    throw new CustomException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            case "assigment":
                //try {
                //    TopicAssigment topicAssigment = mapper.readValue(topicDTO.getData(), TopicAssigment.class);
                //    topicAssigment.setTopicId(createdTopic.getId());
                //    TopicAssigment createdTopicAssigment = topicAssigmentRepository.save(topicAssigment);
                //    createdTopicData = mapper.writeValueAsString(createdTopicAssigment);
                //} catch (JsonProcessingException e) {
                //    throw new CustomException("Error parsing assigment data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                //}

                throw new CustomException("Not implemented yet!", HttpStatus.NOT_IMPLEMENTED);
                //break;
            default:
                throw new CustomException("Topic type not found!", HttpStatus.BAD_REQUEST);
        }

        TopicDTO updatedTopicDTO = TopicMapper.toDTO(updatedTopic);
        updatedTopicDTO.setData(updatedTopicData);
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
