package com.example.crm_gym_microservice.services;

import com.example.crm_gym_microservice.models.*;
import com.example.crm_gym_microservice.repositories.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class TrainingService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final YearWorkloadRepository yearWorkloadRepository;
    private final MonthWorkloadRepository monthWorkloadRepository;
    private final TrainingSessionRepository trainingSessionRepository;

    public TrainingService(
            TrainerWorkloadRepository trainerWorkloadRepository,
            YearWorkloadRepository yearWorkloadRepository,
            MonthWorkloadRepository monthWorkloadRepository,
            TrainingSessionRepository trainingSessionRepository) {
        this.trainerWorkloadRepository = trainerWorkloadRepository;
        this.yearWorkloadRepository = yearWorkloadRepository;
        this.monthWorkloadRepository = monthWorkloadRepository;
        this.trainingSessionRepository = trainingSessionRepository;
    }

    public void handleTrainingSession(TrainingSession trainingSession, String transactionId) {
        TrainerWorkload workload = trainerWorkloadRepository
                .findByTrainerUsername(trainingSession.getTrainerUsername())
                .orElseGet(() -> {
                    TrainerWorkload newWorkload = TrainerWorkload.builder()
                            .trainerUsername(trainingSession.getTrainerUsername())
                            .trainerFirstName(trainingSession.getTrainerFirstName())
                            .trainerLastName(trainingSession.getTrainerLastName())
                            .isActive(trainingSession.getIsActive())
                            .yearWorkloadIds(new ArrayList<>())
                            .build();
                    return trainerWorkloadRepository.save(newWorkload);
                });

        YearWorkload yearWorkload = yearWorkloadRepository
                .findByTrainerWorkloadIdAndTrainingYear(workload.getId(), trainingSession.getTrainingDate().getYear())
                .orElseGet(() -> {
                    YearWorkload newYearWorkload = YearWorkload.builder()
                            .trainingYear(trainingSession.getTrainingDate().getYear())
                            .trainerWorkloadId(workload.getId())
                            .monthWorkloadIds(new ArrayList<>())
                            .build();
                    workload.getYearWorkloadIds().add(yearWorkloadRepository.save(newYearWorkload).getId());
                    trainerWorkloadRepository.save(workload);
                    return newYearWorkload;
                });

        MonthWorkload monthWorkload = monthWorkloadRepository
                .findByYearWorkloadIdAndTrainingMonth(yearWorkload.getId(), trainingSession.getTrainingDate().getMonthValue())
                .orElseGet(() -> {
                    MonthWorkload newMonthWorkload = MonthWorkload.builder()
                            .trainingMonth(trainingSession.getTrainingDate().getMonthValue())
                            .trainingSummaryDuration(0.0)
                            .yearWorkloadId(yearWorkload.getId())
                            .build();
                    yearWorkload.getMonthWorkloadIds().add(monthWorkloadRepository.save(newMonthWorkload).getId());
                    yearWorkloadRepository.save(yearWorkload);
                    return newMonthWorkload;
                });

        if (trainingSession.getActionType() == ActionType.ADD) {
            monthWorkload.setTrainingSummaryDuration(monthWorkload.getTrainingSummaryDuration() + trainingSession.getTrainingDuration());
        } else if (trainingSession.getActionType() == ActionType.DELETE) {
            double newDuration = monthWorkload.getTrainingSummaryDuration() - trainingSession.getTrainingDuration();
            monthWorkload.setTrainingSummaryDuration(Math.max(0, newDuration));
        }

        monthWorkloadRepository.save(monthWorkload);
    }

    public double getMonthlyTrainingDuration(String trainerUsername, int year, int month) {
        Optional<TrainerWorkload> workloadOpt = trainerWorkloadRepository.findByTrainerUsername(trainerUsername);

        if (workloadOpt.isPresent()) {
            TrainerWorkload workload = workloadOpt.get();
            return workload.getYearWorkloadIds().stream()
                    .map(yearWorkloadRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(yearWorkload -> yearWorkload.getTrainingYear() == year)
                    .flatMap(yearWorkload -> yearWorkload.getMonthWorkloadIds().stream())
                    .map(monthWorkloadRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(monthWorkload -> monthWorkload.getTrainingMonth() == month)
                    .mapToDouble(MonthWorkload::getTrainingSummaryDuration)
                    .sum();
        }

        return 0.0;
    }
}
