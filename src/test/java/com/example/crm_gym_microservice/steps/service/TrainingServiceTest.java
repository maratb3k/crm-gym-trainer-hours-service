package com.example.crm_gym_microservice.steps.service;

import com.example.crm_gym_microservice.models.*;
import com.example.crm_gym_microservice.repositories.*;
import com.example.crm_gym_microservice.services.TrainingService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootTest
public class TrainingServiceTest {

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @Autowired
    private YearWorkloadRepository yearWorkloadRepository;

    @Autowired
    private MonthWorkloadRepository monthWorkloadRepository;

    private TrainingSession trainingSession;
    private String trainerUsername;
    private int year;
    private int month;
    private double expectedDuration;

    @Given("a trainer workload exists for {string}")
    public void a_trainer_workload_exists_for(String username) {
        trainerWorkloadRepository.deleteAll();
        yearWorkloadRepository.deleteAll();
        monthWorkloadRepository.deleteAll();

        trainerUsername = username;
        year = 2024;
        month = 12;

        TrainerWorkload trainerWorkload = TrainerWorkload.builder()
                .trainerUsername(username)
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .yearWorkloadIds(new ArrayList<>())
                .build();

        trainerWorkloadRepository.save(trainerWorkload);
    }

    @When("a new training session with action {string} is handled for {string} with date {string} and duration {double}")
    public void a_new_training_session_with_action_is_handled(String action, String username, String date, double duration) {
        trainingSession = TrainingSession.builder()
                .trainerUsername(username)
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.parse(date))
                .trainingDuration(duration)
                .actionType(ActionType.valueOf(action))
                .build();

        trainingService.handleTrainingSession(trainingSession, "txn123");
    }

    @Then("the total monthly duration for {string} in year {int} and month {int} should be {double}")
    public void the_total_monthly_duration_should_be(String username, int year, int month, double expectedDuration) {
        double actualDuration = trainingService.getMonthlyTrainingDuration(username, year, month);
        Assertions.assertEquals(expectedDuration, actualDuration, "The total monthly duration should match the expected value.");
    }
}
