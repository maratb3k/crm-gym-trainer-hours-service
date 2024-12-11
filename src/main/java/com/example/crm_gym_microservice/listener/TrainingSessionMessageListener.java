package com.example.crm_gym_microservice.listener;

import com.example.crm_gym_microservice.dtos.TrainingSessionRequestDTO;
import com.example.crm_gym_microservice.mapper.ModelMapper;
import com.example.crm_gym_microservice.models.TrainingSession;
import com.example.crm_gym_microservice.services.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TrainingSessionMessageListener {

    private final TrainingService trainingService;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final JmsTemplate jmsTemplate;

    @Autowired
    public TrainingSessionMessageListener(TrainingService trainingService, ObjectMapper objectMapper, ModelMapper modelMapper, JmsTemplate jmsTemplate) {
        this.trainingService = trainingService;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = "training-session-queue")
    public void receiveTrainingSessionMessage(Message message) {
        try {
            String transactionId = message.getStringProperty("transactionId");
            if (message instanceof TextMessage textMessage) {
                String jsonMessage = textMessage.getText();
                TrainingSessionRequestDTO trainingSessionRequestDTO = objectMapper.readValue(jsonMessage, TrainingSessionRequestDTO.class);
                TrainingSession trainingSession = modelMapper.mapToTrainingSession(trainingSessionRequestDTO);
                trainingService.handleTrainingSession(trainingSession, transactionId);
            } else {
                log.error("Unsupported message type received: {}", message.getClass().getName());
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
        }
    }

    @JmsListener(destination = "trainer-duration-request-queue")
    public void processTrainerMonthlyDurationRequest(Message message) {
        try {
            if (message instanceof MapMessage mapMessage) {
                String username = mapMessage.getString("username");
                int year = mapMessage.getInt("year");
                int month = mapMessage.getInt("month");
                String transactionId = mapMessage.getString("transactionId");

                log.info("Received request for trainer duration: username={}, year={}, month={}, transactionId={}",
                        username, year, month, transactionId);

                double duration = trainingService.getMonthlyTrainingDuration(username, year, month);
                log.info("Trainer monthly duration for username={}, year={}, month={} is: {}", username, year, month, duration);

                sendResponseToMainService(duration, transactionId);
            } else {
                log.error("Unsupported message type received: {}", message.getClass().getName());
            }
        } catch (Exception e) {
            log.error("Error processing trainer duration request: {}", e.getMessage(), e);
        }
    }

    private void sendResponseToMainService(double duration, String transactionId) {
        try {
            jmsTemplate.send("trainer-duration-response-queue", session -> {
                MapMessage responseMessage = session.createMapMessage();
                responseMessage.setDouble("duration", duration);
                responseMessage.setString("transactionId", transactionId);
                log.info("Sending response to main service: duration={}, transactionId={}", duration, transactionId);
                return responseMessage;
            });
        } catch (Exception e) {
            log.error("Error sending response to main service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send response to main service", e);
        }
    }


}

