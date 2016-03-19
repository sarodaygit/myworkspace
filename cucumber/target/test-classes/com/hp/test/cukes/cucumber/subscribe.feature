Feature: Subscribe for our email marketing service

  Scenario: Subscribe with a valid e-mail address
    Given I want to subscribe to receive important market information
    When I enter a valid address x@y.z
    Then should I get a welcome message

  Scenario: Try to subscribe with an invalid e-mail address
    Given I want to subscribe to receive important market information
    When I enter an invalid address x.y.z
    Then should I get an error message