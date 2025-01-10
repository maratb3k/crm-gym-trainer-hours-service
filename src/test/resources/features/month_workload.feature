Feature: MonthWorkload Repository Component Test

  Scenario: Find a MonthWorkload by YearWorkloadId and TrainingMonth
    Given a MonthWorkload exists with id "1", yearWorkloadId "year130", trainingMonth 7, and trainingSummaryDuration 10.0
    When I search for the MonthWorkload with yearWorkloadId "year130" and trainingMonth 7
    Then I should get a MonthWorkload with id "1" and trainingSummaryDuration 10.0

  Scenario: Return empty result for non-existent workload
    Given no MonthWorkload exists with yearWorkloadId "year130" and trainingMonth 8
    When I search for the MonthWorkload with yearWorkloadId "year130" and trainingMonth 8
    Then I should get no result
