package com.example.crm_gym_microservice.mapper;

import com.example.crm_gym_microservice.dtos.MonthResponse;
import com.example.crm_gym_microservice.dtos.TrainerWorkloadResponse;
import com.example.crm_gym_microservice.dtos.TrainingSessionRequestDTO;
import com.example.crm_gym_microservice.dtos.YearResponse;
import com.example.crm_gym_microservice.models.TrainerWorkload;
import com.example.crm_gym_microservice.models.TrainingSession;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public TrainingSession mapToTrainingSession(TrainingSessionRequestDTO request) {
        return TrainingSession.builder()
                .trainerUsername(request.getTrainerUsername())
                .trainerFirstName(request.getTrainerFirstName())
                .trainerLastName(request.getTrainerLastName())
                .isActive(request.getIsActive())
                .trainingDate(request.getTrainingDate())
                .trainingDuration(request.getTrainingDuration())
                .actionType(request.getActionType())
                .build();
    }
    private TrainerWorkloadResponse mapToTrainerWorkloadResponse(TrainerWorkload workload) {
        return new TrainerWorkloadResponse(
                workload.getTrainerUsername(),
                workload.getTrainerFirstName(),
                workload.getTrainerLastName(),
                workload.getIsActive(),
                workload.getYears().stream()
                        .map(y -> new YearResponse(
                                y.getTrainingYear(),
                                y.getMonths().stream()
                                        .map(m -> new MonthResponse(m.getTrainingMonth(), m.getTrainingSummaryDuration()))
                                        .toList()))
                        .toList());
    }
}
