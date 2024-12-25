package com.example.crm_gym_microservice.models;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "trainingSessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingSession {
    @Id
    private String id;

    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;

    private LocalDate trainingDate;
    private double trainingDuration;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;
}
