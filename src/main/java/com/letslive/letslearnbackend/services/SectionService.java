package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.SectionDTO;
import com.letslive.letslearnbackend.dto.TopicDTO;
import com.letslive.letslearnbackend.entities.Section;
import com.letslive.letslearnbackend.entities.Topic;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.SectionMapper;
import com.letslive.letslearnbackend.mappers.TopicMapper;
import com.letslive.letslearnbackend.repositories.SectionRepository;
import com.letslive.letslearnbackend.repositories.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final TopicRepository topicRepository;
    private final TopicService topicService;

//    public List<SectionDTO> getAllSectionsByCourseID(UUID courseID) {
//        List<Section> sections = sectionRepository.getByCourseId(courseID);
//        return sections.stream().map((section -> SectionMapper.mapToDTO(section))).toList();
//    }

    public SectionDTO createSection(SectionDTO sectionDTO) {
        Section section = sectionRepository.save(SectionMapper.mapToEntity(sectionDTO));
        return SectionMapper.mapToDTO(section);
    }

    public SectionDTO getSectionByID(UUID sectionID) {
        Section section = sectionRepository.findById(sectionID).orElseThrow(() -> new CustomException("Section not found!", HttpStatus.NOT_FOUND));
        return SectionMapper.mapToDTO(section);
    }

    @Transactional
    public SectionDTO updateSection(UUID sectionID, SectionDTO sectionDTO) {
        if (!sectionRepository.existsById(sectionID))
            throw new CustomException("Section not found!", HttpStatus.NOT_FOUND);

        sectionRepository.save(SectionMapper.mapToEntity(sectionDTO));

        // delete topics that don't appear in the dto
        List<Topic> topics = topicRepository.findAllBySectionId(sectionID);
        topics.forEach(topic -> {
            if (!sectionDTO.getTopics().stream().anyMatch(topicDTO -> topicDTO.getId().equals(topic.getId()))) {
                topicRepository.deleteById(topic.getId());
            }
        });

        // save all information in dto
        List<TopicDTO> topicsOfSection = sectionDTO.getTopics();
        if (topicsOfSection != null) {
            sectionDTO.getTopics().forEach(topic -> {
                if (topic.getId() == null || !topicRepository.existsById(topic.getId())) {
                    topicService.createTopic(topic);
                } else topicService.updateTopic(topic);
            });
        }
        Section updatedSection = sectionRepository.findById(sectionID).orElseThrow(() -> new CustomException("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR));
        return SectionMapper.mapToDTO(updatedSection);
    }
}
