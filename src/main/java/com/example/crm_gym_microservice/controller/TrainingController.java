package com.example.crm_gym_microservice.controller;

import com.example.crm_gym_microservice.dtos.*;
import com.example.crm_gym_microservice.logger.TransactionLogger;
import com.example.crm_gym_microservice.mapper.ModelMapper;
import com.example.crm_gym_microservice.models.TrainingSession;
import com.example.crm_gym_microservice.services.TrainingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainings")
public class TrainingController {
    private final TrainingService trainingService;
    private final ModelMapper modelMapper;

    @Autowired
    public TrainingController(TrainingService trainingService, ModelMapper modelMapper) {
        this.trainingService = trainingService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<Void> handleTrainingSession(@RequestBody TrainingSessionRequestDTO request, HttpServletRequest httpRequest) {
        String transactionId = httpRequest.getHeader("TransactionID");
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = TransactionLogger.generateTransactionId();
        }
        TransactionLogger.logRequestDetails(transactionId, httpRequest.getMethod(), httpRequest.getRequestURI(), httpRequest.getParameterMap());
        TrainingSession trainingSession = modelMapper.mapToTrainingSession(request);
        trainingService.handleTrainingSession(trainingSession, transactionId);
        TransactionLogger.logTransactionEnd(transactionId, "Training session handled successfully");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/{year}/{month}")
    public ResponseEntity<Double> getTrainerMonthlyDuration(
            @PathVariable String username, @PathVariable int year, @PathVariable int month, HttpServletRequest request) {
        String transactionId = request.getHeader("TransactionID");
        if (transactionId == null) {
            transactionId = TransactionLogger.generateTransactionId();
        }
        TransactionLogger.logRequestDetails(transactionId, request.getMethod(), request.getRequestURI(), request.getParameterMap());
        double totalDuration = trainingService.getMonthlyTrainingDuration(username, year, month);
        return ResponseEntity.ok(totalDuration);
    }

}
