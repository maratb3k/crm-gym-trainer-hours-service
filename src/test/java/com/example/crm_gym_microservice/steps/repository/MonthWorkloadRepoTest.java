package com.example.crm_gym_microservice.steps.repository;

import com.example.crm_gym_microservice.models.MonthWorkload;
import com.example.crm_gym_microservice.repositories.MonthWorkloadRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MonthWorkloadRepoTest {

    @Mock
    private MonthWorkloadRepository monthWorkloadRepository;

    private Optional<MonthWorkload> result;

    public MonthWorkloadRepoTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("a MonthWorkload exists with id {string}, yearWorkloadId {string}, trainingMonth {int}, and trainingSummaryDuration {double}")
    public void a_month_workload_exists(String id, String yearWorkloadId, int trainingMonth, double duration) {
        MonthWorkload workload = MonthWorkload.builder()
                .id(id)
                .yearWorkloadId(yearWorkloadId)
                .trainingMonth(trainingMonth)
                .trainingSummaryDuration(duration)
                .build();

        when(monthWorkloadRepository.findByYearWorkloadIdAndTrainingMonth(yearWorkloadId, trainingMonth))
                .thenReturn(Optional.of(workload));
    }

    @Given("no MonthWorkload exists with yearWorkloadId {string} and trainingMonth {int}")
    public void no_month_workload_exists(String yearWorkloadId, int trainingMonth) {
        when(monthWorkloadRepository.findByYearWorkloadIdAndTrainingMonth(yearWorkloadId, trainingMonth))
                .thenReturn(Optional.empty());
    }

    @When("I search for the MonthWorkload with yearWorkloadId {string} and trainingMonth {int}")
    public void i_search_for_month_workload(String yearWorkloadId, int trainingMonth) {
        result = monthWorkloadRepository.findByYearWorkloadIdAndTrainingMonth(yearWorkloadId, trainingMonth);
    }

    @Then("I should get a MonthWorkload with id {string} and trainingSummaryDuration {double}")
    public void i_should_get_month_workload(String expectedId, double expectedDuration) {
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(expectedId);
        assertThat(result.get().getTrainingSummaryDuration()).isEqualTo(expectedDuration);
    }

    @Then("I should get no result")
    public void i_should_get_no_result() {
        assertThat(result).isEmpty();
    }
}
