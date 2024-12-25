package com.example.crm_gym_microservice.listener;

import com.example.crm_gym_microservice.dtos.TrainingSessionRequestDTO;
import com.example.crm_gym_microservice.mapper.ModelMapper;
import com.example.crm_gym_microservice.models.TrainingSession;
import com.example.crm_gym_microservice.services.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrainingSessionMessageListenerTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private TrainingSessionMessageListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceiveTrainingSessionMessage_withTextMessage() throws Exception {
        String jsonMessage = "{\"trainerUsername\":\"trainer123\"}";
        String transactionId = "txn123";
        TextMessage textMessage = mock(TextMessage.class);
        TrainingSessionRequestDTO dto = new TrainingSessionRequestDTO();
        TrainingSession trainingSession = new TrainingSession();

        when(textMessage.getText()).thenReturn(jsonMessage);
        when(textMessage.getStringProperty("transactionId")).thenReturn(transactionId);
        when(objectMapper.readValue(jsonMessage, TrainingSessionRequestDTO.class)).thenReturn(dto);
        when(modelMapper.mapToTrainingSession(dto)).thenReturn(trainingSession);

        listener.receiveTrainingSessionMessage(textMessage);

        verify(trainingService, times(1)).handleTrainingSession(trainingSession, transactionId);
    }

    @Test
    void testReceiveTrainingSessionMessage_withUnsupportedMessage() throws Exception {
        Message unsupportedMessage = mock(Message.class);

        listener.receiveTrainingSessionMessage(unsupportedMessage);

        verify(trainingService, never()).handleTrainingSession(any(), any());
    }

    @Test
    void testProcessTrainerMonthlyDurationRequest_withValidMapMessage() throws Exception {
        String username = "trainer123";
        int year = 2024;
        int month = 1;
        String transactionId = "txn123";
        double duration = 5.0;

        MapMessage mapMessage = mock(MapMessage.class);
        when(mapMessage.getString("username")).thenReturn(username);
        when(mapMessage.getInt("year")).thenReturn(year);
        when(mapMessage.getInt("month")).thenReturn(month);
        when(mapMessage.getString("transactionId")).thenReturn(transactionId);
        when(trainingService.getMonthlyTrainingDuration(username, year, month)).thenReturn(duration);

        listener.processTrainerMonthlyDurationRequest(mapMessage);

        verify(trainingService, times(1)).getMonthlyTrainingDuration(username, year, month);
        verify(jmsTemplate, times(1)).send(eq("trainer-duration-response-queue"), any());
    }

    @Test
    void testProcessTrainerMonthlyDurationRequest_withUnsupportedMessage() throws Exception {
        Message unsupportedMessage = mock(Message.class);

        listener.processTrainerMonthlyDurationRequest(unsupportedMessage);

        verify(trainingService, never()).getMonthlyTrainingDuration(anyString(), anyInt(), anyInt());
        verify(jmsTemplate, never()).send(anyString(), any());
    }
}

