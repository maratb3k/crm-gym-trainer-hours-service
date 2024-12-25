package com.example.crm_gym_microservice.repositories;

import com.example.crm_gym_microservice.models.YearWorkload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class YearWorkloadRepositoryTest {

    @Autowired
    private YearWorkloadRepository yearWorkloadRepository;

    @BeforeEach
    void setUp() {
        yearWorkloadRepository.deleteAll();

        YearWorkload workload = YearWorkload.builder()
                .trainerWorkloadId("trainer123")
                .trainingYear(2024)
                .build();
        yearWorkloadRepository.save(workload);
    }

    @Test
    void shouldFindByTrainerWorkloadIdAndTrainingYear() {
        Optional<YearWorkload> result = yearWorkloadRepository.findByTrainerWorkloadIdAndTrainingYear("trainer123", 2024);

        assertTrue(result.isPresent());
        assertEquals("trainer123", result.get().getTrainerWorkloadId());
        assertEquals(2024, result.get().getTrainingYear());
    }

    @Test
    void shouldReturnEmptyWhenNoMatch() {
        Optional<YearWorkload> result = yearWorkloadRepository.findByTrainerWorkloadIdAndTrainingYear("nonexistent", 2023);

        assertTrue(result.isEmpty());
    }
}
