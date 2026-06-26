package org.example.meeting.bdd;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.example.meeting.dto.ReservationCreateRequest;
import org.example.meeting.exception.BusinessConflictException;
import org.example.meeting.exception.ResourceNotFoundException;
import org.example.meeting.model.Reservation;
import org.example.meeting.model.Room;
import org.example.meeting.repository.ReservationRepository;
import org.example.meeting.repository.RoomRepository;
import org.example.meeting.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationSteps {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation lastReservation;
    private Exception lastException;
    private long currentRoomId;

    @Before
    public void reset() {
        reservationRepository.clear();
        roomRepository.clear();
        lastReservation = null;
        lastException = null;
        currentRoomId = 0;
    }

    // ------------------------------------------------------------------ Given

    @Given("a room exists")
    public void aRoomExists() {
        Room room = new Room(null, "Meeting Room A", 10);
        Room saved = roomRepository.save(room);
        currentRoomId = saved.getId();
    }

    @And("an existing reservation")
    public void anExistingReservation() {
        ReservationCreateRequest req = new ReservationCreateRequest();
        req.setRoomId(currentRoomId);
        req.setPersonName("Occupant");
        req.setStartDateTime(LocalDateTime.of(2025, 7, 3, 9, 0));
        req.setEndDateTime(LocalDateTime.of(2025, 7, 3, 11, 0));
        reservationService.create(req);
    }

    // ------------------------------------------------------------------ When

    @When("I create a valid reservation")
    public void iCreateAValidReservation() {
        ReservationCreateRequest req = new ReservationCreateRequest();
        req.setRoomId(currentRoomId);
        req.setPersonName("Alice");
        req.setStartDateTime(LocalDateTime.of(2025, 7, 1, 9, 0));
        req.setEndDateTime(LocalDateTime.of(2025, 7, 1, 11, 0));
        try {
            lastReservation = reservationService.create(req);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("I create a reservation for unknown room")
    public void iCreateAReservationForUnknownRoom() {
        ReservationCreateRequest req = new ReservationCreateRequest();
        req.setRoomId(999L);
        req.setPersonName("Bob");
        req.setStartDateTime(LocalDateTime.of(2025, 7, 2, 9, 0));
        req.setEndDateTime(LocalDateTime.of(2025, 7, 2, 11, 0));
        try {
            lastReservation = reservationService.create(req);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("I create an overlapping reservation")
    public void iCreateAnOverlappingReservation() {
        ReservationCreateRequest req = new ReservationCreateRequest();
        req.setRoomId(currentRoomId);
        req.setPersonName("Charlie");
        req.setStartDateTime(LocalDateTime.of(2025, 7, 3, 10, 0));
        req.setEndDateTime(LocalDateTime.of(2025, 7, 3, 12, 0));
        try {
            lastReservation = reservationService.create(req);
        } catch (Exception e) {
            lastException = e;
        }
    }

    // ------------------------------------------------------------------ Then

    @Then("the reservation is accepted")
    public void theReservationIsAccepted() {
        assertThat(lastException).isNull();
        assertThat(lastReservation).isNotNull();
        assertThat(lastReservation.getStatus()).isEqualTo(Reservation.Status.CONFIRMED);
    }

    @Then("an error NOT FOUND is returned")
    public void anErrorNotFoundIsReturned() {
        assertThat(lastException)
                .isNotNull()
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Then("a CONFLICT error is returned")
    public void aConflictErrorIsReturned() {
        assertThat(lastException)
                .isNotNull()
                .isInstanceOf(BusinessConflictException.class);
    }
}