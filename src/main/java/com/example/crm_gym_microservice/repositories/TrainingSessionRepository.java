package com.example.crm_gym_microservice.repositories;

import com.example.crm_gym_microservice.models.TrainingSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingSessionRepository extends MongoRepository<TrainingSession, String> {
}
