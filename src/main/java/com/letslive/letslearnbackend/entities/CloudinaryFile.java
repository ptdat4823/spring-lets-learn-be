package com.letslive.letslearnbackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CloudinaryFile {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JsonProperty(required = false)
  private UUID id;

  @JsonProperty("name")
  private String name;
  
  @JsonProperty("displayUrl")
  private String displayUrl;

  @JsonProperty("downloadUrl")
  private String downloadUrl;
}