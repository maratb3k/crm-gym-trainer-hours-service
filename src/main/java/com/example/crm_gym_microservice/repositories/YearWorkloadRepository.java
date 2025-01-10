package com.example.crm_gym_microservice.repositories;

import com.example.crm_gym_microservice.models.YearWorkload;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YearWorkloadRepository extends MongoRepository<YearWorkload, String> {
    @Aggregation(pipeline = {
            "{ '$match': { 'trainerWorkloadId': ?0, 'trainingYear': ?1 } }",
            "{ '$limit': 1 }"
    })
    Optional<YearWorkload> findByTrainerWorkloadIdAndTrainingYear(String trainerWorkloadId, int trainingYear);
}
