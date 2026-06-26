Feature: Reservation management

  Scenario: reservation accepted when room exists and slot is free
    Given a room exists
    When I create a valid reservation
    Then the reservation is accepted

  Scenario: reservation rejected when room does not exist
    When I create a reservation for unknown room
    Then an error NOT FOUND is returned

  Scenario: reservation rejected when overlap exists
    Given a room exists
    And an existing reservation
    When I create an overlapping reservation
    Then a CONFLICT error is returned
