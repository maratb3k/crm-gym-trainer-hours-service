package com.example.crm_gym_microservice.mapper;

import com.example.crm_gym_microservice.dtos.MonthResponse;
import com.example.crm_gym_microservice.dtos.TrainerWorkloadResponse;
import com.example.crm_gym_microservice.dtos.TrainingSessionRequestDTO;
import com.example.crm_gym_microservice.dtos.YearResponse;
import com.example.crm_gym_microservice.models.MonthWorkload;
import com.example.crm_gym_microservice.models.TrainerWorkload;
import com.example.crm_gym_microservice.models.TrainingSession;
import com.example.crm_gym_microservice.models.YearWorkload;
import com.example.crm_gym_microservice.repositories.MonthWorkloadRepository;
import com.example.crm_gym_microservice.repositories.YearWorkloadRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModelMapper {

    private final YearWorkloadRepository yearWorkloadRepository;
    private final MonthWorkloadRepository monthWorkloadRepository;

    public ModelMapper(YearWorkloadRepository yearWorkloadRepository,
                       MonthWorkloadRepository monthWorkloadRepository) {
        this.yearWorkloadRepository = yearWorkloadRepository;
        this.monthWorkloadRepository = monthWorkloadRepository;
    }

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

    public TrainerWorkloadResponse mapToTrainerWorkloadResponse(TrainerWorkload workload) {
        List<YearResponse> years = workload.getYearWorkloadIds().stream()
                .map(yearId -> {
                    YearWorkload yearWorkload = yearWorkloadRepository.findById(yearId).orElseThrow();
                    List<MonthResponse> months = yearWorkload.getMonthWorkloadIds().stream()
                            .map(monthId -> {
                                MonthWorkload month = monthWorkloadRepository.findById(monthId).orElseThrow();
                                return new MonthResponse(month.getTrainingMonth(), month.getTrainingSummaryDuration());
                            })
                            .toList();
                    return new YearResponse(yearWorkload.getTrainingYear(), months);
                })
                .toList();

        return new TrainerWorkloadResponse(
                workload.getTrainerUsername(),
                workload.getTrainerFirstName(),
                workload.getTrainerLastName(),
                workload.getIsActive(),
                years
        );
    }
}
