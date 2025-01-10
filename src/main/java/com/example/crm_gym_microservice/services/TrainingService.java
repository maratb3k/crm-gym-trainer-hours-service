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
            TrainingSessionRepository trainingSessionRepository
    ) {
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

        Optional<TrainerWorkload> testworkload = trainerWorkloadRepository.findByTrainerUsername(trainingSession.getTrainerUsername());
        if(testworkload.isPresent()) {
            System.out.println(testworkload.get().getYearWorkloadIds());
        }

        YearWorkload yearWorkload = yearWorkloadRepository
                .findByTrainerWorkloadIdAndTrainingYear(workload.getId(), trainingSession.getTrainingDate().getYear())
                .orElseGet(() -> {
                    YearWorkload newYearWorkload = YearWorkload.builder()
                            .trainingYear(trainingSession.getTrainingDate().getYear())
                            .trainerWorkloadId(workload.getId())
                            .monthWorkloadIds(new ArrayList<>())
                            .build();
                    YearWorkload savedYearWorkload = yearWorkloadRepository.save(newYearWorkload);
                    workload.getYearWorkloadIds().add(savedYearWorkload.getId());
                    trainerWorkloadRepository.save(workload);
                    return savedYearWorkload;
                });

        MonthWorkload monthWorkload = monthWorkloadRepository
                .findByYearWorkloadIdAndTrainingMonth(yearWorkload.getId(), trainingSession.getTrainingDate().getMonthValue())
                .orElseGet(() -> {
                    MonthWorkload newMonthWorkload = MonthWorkload.builder()
                            .trainingMonth(trainingSession.getTrainingDate().getMonthValue())
                            .trainingSummaryDuration(0.0)
                            .yearWorkloadId(yearWorkload.getId())
                            .build();
                    MonthWorkload savedMonthWorkload = monthWorkloadRepository.save(newMonthWorkload);
                    yearWorkload.getMonthWorkloadIds().add(savedMonthWorkload.getId());
                    yearWorkloadRepository.save(yearWorkload);
                    return savedMonthWorkload;
                });

        System.out.println("ADD " + (trainingSession.getActionType() == ActionType.ADD));
        if (trainingSession.getActionType() == ActionType.ADD) {
            System.out.println("TrainingService ADD" + transactionId);
            monthWorkload.setTrainingSummaryDuration(monthWorkload.getTrainingSummaryDuration() + trainingSession.getTrainingDuration());
            System.out.println("getTrainingSummaryDuration " + monthWorkload.getTrainingSummaryDuration());
        } else if (trainingSession.getActionType() == ActionType.DELETE) {
            double newDuration = monthWorkload.getTrainingSummaryDuration() - trainingSession.getTrainingDuration();
            monthWorkload.setTrainingSummaryDuration(Math.max(0, newDuration));
        }

        System.out.println("TrainingService monthWorkload " + monthWorkload.getTrainingMonth());
        System.out.println("TrainingService monthWorkload time" + monthWorkload.getTrainingSummaryDuration());
        System.out.println("TrainingService monthWorkload id" + monthWorkload.getId());
        monthWorkloadRepository.save(monthWorkload);

        Optional<TrainerWorkload> workloadOpt = trainerWorkloadRepository.findByTrainerUsername(workload.getTrainerUsername());
        if (workloadOpt.isPresent()) {
            System.out.println("TrainingService workload check " + workloadOpt.get().getId());
            System.out.println("TrainingService workload check " + workloadOpt.get().getTrainerUsername());
            System.out.println("TrainingService workload check " + workloadOpt.get().getYearWorkloadIds());
            System.out.println("checkkkkk");
            for (String tWorkload : workloadOpt.get().getYearWorkloadIds()) {
                Optional<YearWorkload> optYear = yearWorkloadRepository.findById(tWorkload);
                if (optYear.isPresent()) {
                    System.out.println("TrainingService yearWorkload eck " + optYear.get().getId());
                } else {
                    System.out.println(" noooo TrainingService yearWorkload eck ");
                }
            }

        }
    }

    public double getMonthlyTrainingDuration(String trainerUsername, int year, int month) {
        Optional<TrainerWorkload> workloadOpt = trainerWorkloadRepository.findByTrainerUsername(trainerUsername);

        System.out.println("getMonthlyTrainingDuration trainerUsername: " + trainerUsername);
        System.out.println("getMonthlyTrainingDuration year: " + year);
        System.out.println("getMonthlyTrainingDuration month: " + month);

        if (workloadOpt.isPresent()) {
            TrainerWorkload workload = workloadOpt.get();
            System.out.println("TrainerWorkload name: " + workload.getTrainerUsername());
            System.out.println("TrainerWorkload ID: " + workload.getId());
            System.out.println("TrainerWorkload years: " + workload.getYearWorkloadIds().size());

            for (String ids : workload.getYearWorkloadIds()) {
                System.out.println("YearWorkload ID: " + ids);
                Optional<YearWorkload> yearWorkload = yearWorkloadRepository.findById(ids);
                if (yearWorkload.isPresent()) {
                    System.out.println("YearWorkload present: " + yearWorkload.get().getTrainingYear());
                } else {
                    System.out.println("YearWorkload not found");
                }
            }


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

        System.out.println("getMonthlyTrainingDuration is 0!!!!!!!!!!!!");
        return 0.0;
    }
}
