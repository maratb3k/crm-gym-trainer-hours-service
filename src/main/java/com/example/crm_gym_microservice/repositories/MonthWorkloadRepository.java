package com.example.crm_gym_microservice.repositories;

import com.example.crm_gym_microservice.models.MonthWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthWorkloadRepository extends MongoRepository<MonthWorkload,String> {
    @Query(value = "{ 'yearWorkloadId': ?0, 'trainingMonth': ?1 }")
    Optional<MonthWorkload> findByYearWorkloadIdAndTrainingMonth(String yearWorkloadId, int trainingMonth);
}
