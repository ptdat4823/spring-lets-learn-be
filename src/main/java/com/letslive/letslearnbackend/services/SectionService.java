package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.SectionDTO;
import com.letslive.letslearnbackend.entities.Section;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.SectionMapper;
import com.letslive.letslearnbackend.repositories.SectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;

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
}
