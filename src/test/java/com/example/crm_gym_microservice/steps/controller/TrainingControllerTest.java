package com.example.crm_gym_microservice.steps.controller;

import com.example.crm_gym_microservice.controller.TrainingController;
import com.example.crm_gym_microservice.dtos.TrainingSessionRequestDTO;
import com.example.crm_gym_microservice.mapper.ModelMapper;
import com.example.crm_gym_microservice.models.ActionType;
import com.example.crm_gym_microservice.models.TrainingSession;
import com.example.crm_gym_microservice.services.TrainingService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingControllerTest {

    @InjectMocks
    private TrainingController trainingController;

    @Mock
    private TrainingService trainingService;

    @Mock
    private ModelMapper modelMapper;

    private ResponseEntity<?> response;
    private TrainingSessionRequestDTO requestDTO;
    private double expectedDuration;

    public TrainingControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("a valid training session request")
    public void a_valid_training_session_request() {
        requestDTO = TrainingSessionRequestDTO.builder()
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
                .trainingDate(LocalDate.parse("2024-12-01"))
                .trainingDuration(2.0)
                .actionType(ActionType.ADD)
                .build();

        when(modelMapper.mapToTrainingSession(requestDTO)).thenReturn(mockSession);
        doNothing().when(trainingService).handleTrainingSession(eq(mockSession), anyString());
    }

    @When("the client sends a POST request to {string}")
    public void the_client_sends_a_post_request_to(String endpoint) {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("TransactionID")).thenReturn("txn123");
        response = trainingController.handleTrainingSession(requestDTO, mockRequest);
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(int statusCode) {
        Assertions.assertEquals(statusCode, response.getStatusCodeValue());
    }

    @Given("a trainer username {string} with year {int} and month {int}")
    public void a_trainer_username_with_year_and_month(String username, int year, int month) {
        expectedDuration = 2.0;
        when(trainingService.getMonthlyTrainingDuration(eq(username), eq(year), eq(month)))
                .thenReturn(expectedDuration);
    }

    @When("the client sends a GET request to {string}")
    public void the_client_sends_a_get_request_to(String endpoint) {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("TransactionID")).thenReturn("txn123");
        response = trainingController.getTrainerMonthlyDuration("trainer123", 2024, 12, mockRequest);
    }

    @Then("the response should contain {double}")
    public void the_response_should_contain_the_duration(double duration) {
        Assertions.assertEquals(duration, response.getBody());
    }
}
