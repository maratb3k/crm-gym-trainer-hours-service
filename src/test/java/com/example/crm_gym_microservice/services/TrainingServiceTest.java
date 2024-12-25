package com.example.crm_gym_microservice.services;

import com.example.crm_gym_microservice.models.*;
import com.example.crm_gym_microservice.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @Mock
    private YearWorkloadRepository yearWorkloadRepository;

    @Mock
    private MonthWorkloadRepository monthWorkloadRepository;

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTraining() {
        TrainingSession session = TrainingSession.builder()
                .trainerUsername("trainer123")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 1, 15))
                .trainingDuration(2.0)
                .actionType(ActionType.ADD)
                .build();

        TrainerWorkload mockWorkload = TrainerWorkload.builder()
                .id("workload1")
                .trainerUsername("trainer123")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .yearWorkloadIds(new ArrayList<>())
                .build();

        YearWorkload mockYearWorkload = YearWorkload.builder()
                .id("year1")
                .trainingYear(2024)
                .trainerWorkloadId("workload1")
                .monthWorkloadIds(new ArrayList<>())
                .build();

        MonthWorkload mockMonthWorkload = MonthWorkload.builder()
                .id("month1")
                .trainingMonth(1)
                .trainingSummaryDuration(0.0)
                .yearWorkloadId("year1")
                .build();

        when(trainerWorkloadRepository.findByTrainerUsername("trainer123")).thenReturn(Optional.of(mockWorkload));
        when(yearWorkloadRepository.findByTrainerWorkloadIdAndTrainingYear("workload1", 2024)).thenReturn(Optional.of(mockYearWorkload));
        when(monthWorkloadRepository.findByYearWorkloadIdAndTrainingMonth("year1", 1)).thenReturn(Optional.of(mockMonthWorkload));

        trainingService.handleTrainingSession(session, "txn123");

        verify(monthWorkloadRepository, times(1)).save(any(MonthWorkload.class));
        assertEquals(2.0, mockMonthWorkload.getTrainingSummaryDuration());
    }

    @Test
    void testDeleteTraining() {
        TrainingSession session = TrainingSession.builder()
                .trainerUsername("trainer123")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 1, 15))
                .trainingDuration(1.0)
                .actionType(ActionType.DELETE)
                .build();

        TrainerWorkload mockWorkload = TrainerWorkload.builder()
                .id("workload1")
                .trainerUsername("trainer123")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .yearWorkloadIds(new ArrayList<>())
                .build();

        YearWorkload mockYearWorkload = YearWorkload.builder()
                .id("year1")
                .trainingYear(2024)
                .trainerWorkloadId("workload1")
                .monthWorkloadIds(new ArrayList<>())
                .build();

        MonthWorkload mockMonthWorkload = MonthWorkload.builder()
                .id("month1")
                .trainingMonth(1)
                .trainingSummaryDuration(2.0)
                .yearWorkloadId("year1")
                .build();

        when(trainerWorkloadRepository.findByTrainerUsername("trainer123")).thenReturn(Optional.of(mockWorkload));
        when(yearWorkloadRepository.findByTrainerWorkloadIdAndTrainingYear("workload1", 2024)).thenReturn(Optional.of(mockYearWorkload));
        when(monthWorkloadRepository.findByYearWorkloadIdAndTrainingMonth("year1", 1)).thenReturn(Optional.of(mockMonthWorkload));

        trainingService.handleTrainingSession(session, "txn123");

        verify(monthWorkloadRepository, times(1)).save(any(MonthWorkload.class));
        assertEquals(1.0, mockMonthWorkload.getTrainingSummaryDuration());
    }

    @Test
    void testGetMonthlyTrainingDuration() {
        MonthWorkload mockMonthWorkload = MonthWorkload.builder()
                .id("month1")
                .trainingMonth(1)
                .trainingSummaryDuration(2.0)
                .yearWorkloadId("year1")
                .build();

        YearWorkload mockYearWorkload = YearWorkload.builder()
                .id("year1")
                .trainingYear(2024)
                .trainerWorkloadId("workload1")
                .monthWorkloadIds(List.of("month1"))
                .build();

        TrainerWorkload mockWorkload = TrainerWorkload.builder()
                .id("workload1")
                .trainerUsername("trainer123")
                .yearWorkloadIds(List.of("year1"))
                .build();

        when(trainerWorkloadRepository.findByTrainerUsername("trainer123")).thenReturn(Optional.of(mockWorkload));
        when(yearWorkloadRepository.findById("year1")).thenReturn(Optional.of(mockYearWorkload));
        when(monthWorkloadRepository.findById("month1")).thenReturn(Optional.of(mockMonthWorkload));

        double totalDuration = trainingService.getMonthlyTrainingDuration("trainer123", 2024, 1);

        assertEquals(2.0, totalDuration);
    }
}

