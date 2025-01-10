package com.example.crm_gym_microservice.steps.listener;

import com.example.crm_gym_microservice.dtos.TrainingSessionRequestDTO;
import com.example.crm_gym_microservice.listener.TrainingSessionMessageListener;
import com.example.crm_gym_microservice.mapper.ModelMapper;
import com.example.crm_gym_microservice.models.ActionType;
import com.example.crm_gym_microservice.models.TrainingSession;
import com.example.crm_gym_microservice.repositories.MonthWorkloadRepository;
import com.example.crm_gym_microservice.repositories.YearWorkloadRepository;
import com.example.crm_gym_microservice.services.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.jms.MapMessage;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
public class TrainingSessionMessageListenerTest {

    @InjectMocks
    private TrainingSessionMessageListener messageListener;

    @Mock
    private TrainingService trainingService;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private YearWorkloadRepository yearWorkloadRepository;

    @Mock
    private MonthWorkloadRepository monthWorkloadRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ObjectMapper objectMapper;

    private TextMessage textMessage;
    private MapMessage mapMessage;

    public TrainingSessionMessageListenerTest() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Given("a valid training session message is sent")
    public void a_valid_training_session_message_is_sent() throws Exception {
        TrainingSessionRequestDTO requestDTO = TrainingSessionRequestDTO.builder()
                .trainerUsername("trainer123")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.parse("2024-12-01"))
                .trainingDuration(2.0)
                .actionType(ActionType.ADD)
                .build();

        TrainingSession mockSession = TrainingSession.builder()
                .trainerUsername("trainer123")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.parse("2024-12-01"))
                .trainingDuration(2.0)
                .actionType(ActionType.ADD)
                .build();

        textMessage = mock(TextMessage.class);
        when(textMessage.getStringProperty("transactionId")).thenReturn("txn123");
        when(textMessage.getText()).thenReturn(objectMapper.writeValueAsString(requestDTO));

        when(modelMapper.mapToTrainingSession(any(TrainingSessionRequestDTO.class))).thenReturn(mockSession);
        doNothing().when(trainingService).handleTrainingSession(mockSession, "txn123");
    }


    @When("the message is received by the listener")
    public void the_message_is_received_by_the_listener() throws Exception {
        messageListener.receiveTrainingSessionMessage(textMessage);
    }

    @Then("the training session should be processed successfully")
    public void the_training_session_should_be_processed_successfully() {
        verify(trainingService, times(1)).handleTrainingSession(any(TrainingSession.class), eq("txn123"));
    }

    @Given("a valid trainer duration request message is sent")
    public void a_valid_trainer_duration_request_message_is_sent() throws Exception {
        mapMessage = mock(MapMessage.class);
        when(mapMessage.getString("username")).thenReturn("trainer123");
        when(mapMessage.getInt("year")).thenReturn(2024);
        when(mapMessage.getInt("month")).thenReturn(12);
        when(mapMessage.getString("transactionId")).thenReturn("txn124");

        when(trainingService.getMonthlyTrainingDuration("trainer123", 2024, 12)).thenReturn(2.0);
    }

    @When("the request message is received by the listener")
    public void the_request_message_is_received_by_the_listener() throws Exception {
        messageListener.processTrainerMonthlyDurationRequest(mapMessage);
    }

    @Then("the correct trainer duration should be sent as a response")
    public void the_correct_trainer_duration_should_be_sent_as_a_response() {
        verify(jmsTemplate, times(1)).send(eq("trainer-duration-response-queue"), any());
    }
}
