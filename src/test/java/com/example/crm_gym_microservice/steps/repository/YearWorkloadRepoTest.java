package com.example.crm_gym_microservice.steps.repository;

import com.example.crm_gym_microservice.models.YearWorkload;
import com.example.crm_gym_microservice.repositories.YearWorkloadRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class YearWorkloadRepoTest {

    @Mock
    private YearWorkloadRepository yearWorkloadRepository;

    private Optional<YearWorkload> searchResult;

    public YearWorkloadRepoTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("the following year workloads exist:")
    public void the_following_year_workloads_exist(DataTable dataTable) {
        List<YearWorkload> yearWorkloads = dataTable.asMaps().stream()
                .map(row -> YearWorkload.builder()
                        .trainerWorkloadId(row.get("trainerWorkloadId"))
                        .trainingYear(Integer.parseInt(row.get("trainingYear")))
                        .build())
                .collect(Collectors.toList());

        for (YearWorkload workload : yearWorkloads) {
            when(yearWorkloadRepository.findByTrainerWorkloadIdAndTrainingYear(
                    eq(workload.getTrainerWorkloadId()),
                    eq(workload.getTrainingYear())
            )).thenReturn(Optional.of(workload));
        }

        when(yearWorkloadRepository.findByTrainerWorkloadIdAndTrainingYear(
                eq("8765432123"), eq(2025)
        )).thenReturn(Optional.empty());
    }

    @When("I search for year workload by trainer workload ID {string} and training year {int}")
    public void i_search_for_year_workload_by_trainer_workload_id_and_training_year(String trainerWorkloadId, int trainingYear) {
        searchResult = yearWorkloadRepository.findByTrainerWorkloadIdAndTrainingYear(trainerWorkloadId, trainingYear);
    }

    @Then("the result should be present with trainer workload ID {string} and training year {int}")
    public void the_result_should_be_present_with_trainer_workload_id_and_training_year(String trainerWorkloadId, int trainingYear) {
        assertThat(searchResult).isPresent();
        assertThat(searchResult.get().getTrainerWorkloadId()).isEqualTo(trainerWorkloadId);
        assertThat(searchResult.get().getTrainingYear()).isEqualTo(trainingYear);
    }

    @Then("the result for year workload should be empty")
    public void the_result_should_be_empty() {
        assertThat(searchResult).isEmpty();
    }
}
