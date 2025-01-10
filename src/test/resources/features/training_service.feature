Feature: Training Service

  Scenario: Add training session and get monthly training duration
    Given a trainer workload exists for "trainer123"
    When a new training session with action "ADD" is handled for "trainer123" with date "2024-12-01" and duration 2.0
    Then the total monthly duration for "trainer123" in year 2024 and month 12 should be 2.0
