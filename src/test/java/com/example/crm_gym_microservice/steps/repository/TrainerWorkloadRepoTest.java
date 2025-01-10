package com.example.crm_gym_microservice.steps.repository;

import com.example.crm_gym_microservice.repositories.TrainerWorkloadRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadRepoTest {

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    private Optional<Double> searchResult;

    public TrainerWorkloadRepoTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("the following trainer workloads exist:")
    public void the_following_trainer_workloads_exist(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, Map<Integer, Map<Integer, Double>>> workloadsByTrainer = new HashMap<>();

        for (Map<String, String> row : rows) {
            String trainerUsername = row.get("trainerUsername");
            int trainingYear = Integer.parseInt(row.get("trainingYear"));
            int trainingMonth = Integer.parseInt(row.get("trainingMonth"));
            double trainingSummaryDuration = Double.parseDouble(row.get("trainingSummaryDuration"));

            workloadsByTrainer
                    .computeIfAbsent(trainerUsername, k -> new HashMap<>())
                    .computeIfAbsent(trainingYear, y -> new HashMap<>())
                    .put(trainingMonth, trainingSummaryDuration);
        }

        when(trainerWorkloadRepository.findTotalTrainingDuration(
                anyString(),
                anyInt(),
                anyInt()
        )).thenAnswer(invocation -> {
            String trainerUsername = invocation.getArgument(0);
            int year = invocation.getArgument(1);
            int month = invocation.getArgument(2);

            Double duration = Optional.ofNullable(workloadsByTrainer.get(trainerUsername))
                    .map(yearMap -> yearMap.get(year))
                    .map(monthMap -> monthMap.get(month))
                    .orElse(null);

            return Optional.ofNullable(duration);
        });
    }

    @When("I search for total training duration for {string} in year {int} and month {int}")
    public void i_search_for_total_training_duration_for(String trainerUsername, int year, int month) {
        searchResult = trainerWorkloadRepository.findTotalTrainingDuration(trainerUsername, year, month);
    }

    @Then("the result should be {double}")
    public void the_result_should_be(Double expectedDuration) {
        assertThat(searchResult).isPresent();
        assertThat(searchResult.get()).isEqualTo(expectedDuration);
    }

    @Then("the result for trainer workload should be empty")
    public void the_result_for_trainer_workload_should_be_empty() {
        assertThat(searchResult).isEmpty();
    }
}
