package com.example.crm_gym_microservice.repositories;

import com.example.crm_gym_microservice.models.TrainerWorkload;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

@Repository
public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkload, String> {
    @Aggregation(pipeline = {
            "{ '$match': { 'trainerUsername': ?0 } }",
            "{ '$limit': 1 }"
    })
    Optional<TrainerWorkload> findByTrainerUsername(String trainerUsername);

    @Aggregation(pipeline = {
            "{ '$match': { 'trainerUsername': ?0 } }",
            "{ '$lookup': { " +
                    "'from': 'yearWorkloads', " +
                    "'let': { 'yearIds': '$yearWorkloadIds' }, " +
                    "'pipeline': [ " +
                    "{ '$match': { '$expr': { '$in': [ '$_id', { '$map': { 'input': '$$yearIds', 'as': 'id', 'in': { '$toObjectId': '$$id' } } } ] } } } " +
                    "], " +
                    "'as': 'years' } }",
            "{ '$unwind': '$years' }",
            "{ '$match': { 'years.trainingYear': ?1 } }",
            "{ '$lookup': { " +
                    "'from': 'monthWorkloads', " +
                    "'let': { 'monthIds': '$years.monthWorkloadIds' }, " +
                    "'pipeline': [ " +
                    "{ '$match': { '$expr': { '$in': [ '$_id', { '$map': { 'input': '$$monthIds', 'as': 'id', 'in': { '$toObjectId': '$$id' } } } ] } } } " +
                    "], " +
                    "'as': 'months' } }",
            "{ '$unwind': '$months' }",
            "{ '$match': { 'months.trainingMonth': ?2 } }",
            "{ '$group': { '_id': null, 'totalDuration': { '$sum': '$months.trainingSummaryDuration' } } }"
    })
    Optional<Double> findTotalTrainingDuration(String trainerUsername, int year, int month);
}
