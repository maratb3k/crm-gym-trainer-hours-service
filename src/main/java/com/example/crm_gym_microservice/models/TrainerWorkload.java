package com.example.crm_gym_microservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "trainerWorkloads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndex(name = "TrainerFirstNameLastNameIndex", def = "{'trainerFirstName': 1, 'trainerLastName': 1}")
public class TrainerWorkload {
    @Id
    private String id;
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;

    private List<String> yearWorkloadIds = new ArrayList<>();
}
