Feature: Training Session Message Listener

  Scenario: Process a training session message
    Given a valid training session message is sent
    When the message is received by the listener
    Then the training session should be processed successfully

  Scenario: Process a trainer monthly duration request message
    Given a valid trainer duration request message is sent
    When the request message is received by the listener
    Then the correct trainer duration should be sent as a response
