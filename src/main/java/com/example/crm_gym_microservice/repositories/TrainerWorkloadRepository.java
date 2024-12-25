package com.example.crm_gym_microservice.repositories;

import com.example.crm_gym_microservice.models.TrainerWorkload;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkload, String> {
    Optional<TrainerWorkload> findByTrainerUsername(String trainerUsername);

    @Aggregation(pipeline = {
            "{ '$match': { 'trainerUsername': ?0 } }",
            "{ '$unwind': '$years' }",
            "{ '$match': { 'years.trainingYear': ?1 } }",
            "{ '$unwind': '$years.months' }",
            "{ '$match': { 'years.months.trainingMonth': ?2 } }",
            "{ '$group': { '_id': null, 'totalDuration': { '$sum': '$years.months.trainingSummaryDuration' } } }"
    })
    Optional<Double> findTotalTrainingDuration(String trainerUsername, int year, int month);
}
