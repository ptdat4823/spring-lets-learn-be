package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.SectionDTO;
import com.letslive.letslearnbackend.services.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/section")
@RequiredArgsConstructor
@Validated
public class SectionController {
    private final SectionService sectionService;

//    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<SectionDTO>> getAllSectionsByCourseID(@RequestParam UUID courseID) {
//        List<SectionDTO> sections = sectionService.getAllSectionsByCourseID(courseID);
//        return ResponseEntity.ok(sections);
//    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionDTO> getSectionByID(@PathVariable UUID id) {
        SectionDTO section = sectionService.getSectionByID(id);
        return ResponseEntity.ok(section);
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionDTO> createSection(@RequestBody @Valid SectionDTO sectionDTO) {
        SectionDTO createdSection = sectionService.createSection(sectionDTO);
        return ResponseEntity.ok(createdSection);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionDTO> updateSection(@PathVariable UUID id, @RequestBody @Valid SectionDTO sectionDTO) {
        return ResponseEntity.ok(sectionService.updateSection(id, sectionDTO));
    }
}
