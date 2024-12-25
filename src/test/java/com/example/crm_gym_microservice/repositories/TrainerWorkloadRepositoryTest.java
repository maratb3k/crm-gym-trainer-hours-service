package com.example.crm_gym_microservice.repositories;

import com.example.crm_gym_microservice.models.TrainerWorkload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class TrainerWorkloadRepositoryTest {

    @Autowired
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @BeforeEach
    void setUp() {
        trainerWorkloadRepository.deleteAll();

        TrainerWorkload workload = TrainerWorkload.builder()
                .trainerUsername("trainer123")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .yearWorkloadIds(List.of("year2024_1", "year2024_2"))
                .build();

        trainerWorkloadRepository.save(workload);
    }

    @Test
    void shouldFindTrainerWorkloadByUsername() {
        Optional<TrainerWorkload> workloadOptional = trainerWorkloadRepository.findByTrainerUsername("trainer123");

        assertTrue(workloadOptional.isPresent());
        TrainerWorkload workload = workloadOptional.get();
        assertEquals("trainer123", workload.getTrainerUsername());
        assertEquals("John", workload.getTrainerFirstName());
        assertEquals("Doe", workload.getTrainerLastName());
        assertTrue(workload.getIsActive());
        assertEquals(2, workload.getYearWorkloadIds().size());
        assertEquals("year2024_1", workload.getYearWorkloadIds().get(0));
        assertEquals("year2024_2", workload.getYearWorkloadIds().get(1));
    }

    @Test
    void shouldReturnEmptyForNonExistentTrainer() {
        Optional<TrainerWorkload> workloadOptional = trainerWorkloadRepository.findByTrainerUsername("nonexistent_trainer");

        assertTrue(workloadOptional.isEmpty());
    }
}
