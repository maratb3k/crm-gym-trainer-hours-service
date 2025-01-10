Feature: Training Controller

  Scenario: Handle a training session request
    Given a valid training session request
    When the client sends a POST request to "/trainings"
    Then the response status should be 200

  Scenario: Get trainer's monthly duration
    Given a trainer username "trainer123" with year 2024 and month 12
    When the client sends a GET request to "/trainings/trainer123/2024/12"
    Then the response status should be 200
    Then the response should contain 2.0
