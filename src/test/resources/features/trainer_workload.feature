Feature: Trainer Workload Repository Integration Test

  Scenario: Find total training duration for a trainer in a specific month and year
    Given the following trainer workloads exist:
      | trainerUsername | trainingYear | trainingMonth | trainingSummaryDuration |
      | trainer124      | 2024         | 12            | 35.0                    |
      | trainer124      | 2024         | 11            | 5.0                     |
    When I search for total training duration for "trainer124" in year 2024 and month 12
    Then the result should be 35.0

  Scenario: Return empty result for non-existent workload
    Given the following trainer workloads exist:
      | trainerUsername | trainingYear | trainingMonth | trainingSummaryDuration |
      | trainer124      | 2024         | 12            | 30.0                    |
      | trainer124      | 2024         | 11            | 15.0                     |
    When I search for total training duration for "trainer124" in year 2025 and month 1
    Then the result for trainer workload should be empty
