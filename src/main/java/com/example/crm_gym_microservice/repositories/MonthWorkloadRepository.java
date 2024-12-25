package com.example.crm_gym_microservice.repositories;

import com.example.crm_gym_microservice.models.MonthWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface MonthWorkloadRepository extends MongoRepository<MonthWorkload,String> {
    Optional<MonthWorkload> findByYearWorkloadIdAndTrainingMonth(String yearWorkloadId, int trainingMonth);
}
