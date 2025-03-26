package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.SectionDTO;
import com.letslive.letslearnbackend.entities.Section;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.SectionMapper;
import com.letslive.letslearnbackend.mappers.TopicMapper;
import com.letslive.letslearnbackend.repositories.SectionRepository;
import com.letslive.letslearnbackend.repositories.TopicRepository;
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

//    public List<SectionDTO> getAllSectionsByCourseID(UUID courseID) {
//        List<Section> sections = sectionRepository.getByCourseId(courseID);
//        return sections.stream().map((section -> SectionMapper.mapToDTO(section))).toList();
//    }

    public SectionDTO saveSection(SectionDTO sectionDTO) {
        Section section = sectionRepository.save(SectionMapper.mapToEntity(sectionDTO));
        return SectionMapper.mapToDTO(section);
    }

    public SectionDTO getSectionByID(UUID sectionID) {
        Section section = sectionRepository.findById(sectionID).orElseThrow(() -> new CustomException("Section not found!", HttpStatus.NOT_FOUND));
        return SectionMapper.mapToDTO(section);
    }

    public SectionDTO updateSection(UUID sectionID, SectionDTO sectionDTO) {
        sectionRepository.findById(sectionID).orElseThrow(() -> new CustomException("Section not found!", HttpStatus.NOT_FOUND));
        sectionRepository.save(SectionMapper.mapToEntity(sectionDTO));

        // soft delete topics that don't appear in the dto
        topicRepository.findAllBySectionId(sectionID).forEach(topic -> {
            if (sectionDTO.getTopics().stream().anyMatch(topicDTO -> topicDTO.getId().equals(topic.getId()))) {
                topic.setSectionId(null);
                topicRepository.save(topic);
            }
        });

        sectionDTO.getTopics().stream().forEach(topic -> {
            topicRepository.save(TopicMapper.toEntity(topic));
        });

        return SectionMapper.mapToDTO(sectionRepository.findById(sectionID).orElseThrow(() -> new CustomException("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR)));
    }
}
