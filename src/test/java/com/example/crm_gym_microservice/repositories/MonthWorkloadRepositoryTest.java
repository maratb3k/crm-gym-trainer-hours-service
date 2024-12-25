package com.example.crm_gym_microservice.repositories;

import com.example.crm_gym_microservice.models.MonthWorkload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class MonthWorkloadRepositoryTest {

    @Autowired
    private MonthWorkloadRepository monthWorkloadRepository;

    @Test
    public void testFindByYearWorkloadIdAndTrainingMonth() {
        MonthWorkload workload = MonthWorkload.builder()
                .id("1")
                .yearWorkloadId("year123")
                .trainingMonth(7)
                .trainingSummaryDuration(10.0)
                .build();
        monthWorkloadRepository.save(workload);

        Optional<MonthWorkload> result = monthWorkloadRepository.findByYearWorkloadIdAndTrainingMonth("year123", 7);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("1");
        assertThat(result.get().getTrainingMonth()).isEqualTo(7);
        assertThat(result.get().getTrainingSummaryDuration()).isEqualTo(10.0);
    }

    @Test
    public void testFindByYearWorkloadIdAndTrainingMonth_NoResult() {
        Optional<MonthWorkload> result = monthWorkloadRepository.findByYearWorkloadIdAndTrainingMonth("nonexistent", 7);

        assertThat(result).isEmpty();
    }
}
