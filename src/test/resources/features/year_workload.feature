Feature: Year Workload Repository Component Test

  Scenario: Find existing year workload
    Given the following year workloads exist:
      | trainerWorkloadId | trainingYear |
      | 8765432123        | 2024         |
      | 8765432123        | 2023         |
    When I search for year workload by trainer workload ID "8765432123" and training year 2024
    Then the result should be present with trainer workload ID "8765432123" and training year 2024

  Scenario: Return empty result for non-existent year workload
    Given the following year workloads exist:
      | trainerWorkloadId | trainingYear |
      | 8765432123        | 2024         |
      | 8765432123        | 2023         |
    When I search for year workload by trainer workload ID "8765432123" and training year 2025
    Then the result for year workload should be empty
