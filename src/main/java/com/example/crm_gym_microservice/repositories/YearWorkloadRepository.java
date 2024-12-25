package com.example.crm_gym_microservice.repositories;

import com.example.crm_gym_microservice.models.YearWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface YearWorkloadRepository extends MongoRepository<YearWorkload, String> {
    Optional<YearWorkload> findByTrainerWorkloadIdAndTrainingYear(String trainerWorkloadId, int trainingYear);
}
