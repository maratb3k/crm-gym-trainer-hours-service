package com.example.crm_gym_microservice.dtos;

import com.example.crm_gym_microservice.models.ActionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSessionRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;
    private LocalDate trainingDate;
    private double trainingDuration;
    private ActionType actionType;
}