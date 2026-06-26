package org.example.meeting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.meeting.dto.ReservationCreateRequest;
import org.example.meeting.exception.BusinessConflictException;
import org.example.meeting.exception.ResourceNotFoundException;
import org.example.meeting.model.Reservation;
import org.example.meeting.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    private static final String BODY_VALIDE = """
            {
              "roomId": 1,
              "personName": "Alice",
              "startDateTime": "2025-06-10T09:00:00",
              "endDateTime": "2025-06-10T11:00:00"
            }
            """;

    @Test
    @DisplayName("POST /api/reservations — création correcte retourne 201")
    void postReservation_donnéesValides_retourne201() throws Exception {
        Reservation reservation = new Reservation(
                1L, 1L, "Alice",
                LocalDateTime.of(2025, 6, 10, 9, 0),
                LocalDateTime.of(2025, 6, 10, 11, 0));
        when(reservationService.create(any())).thenReturn(reservation);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.personName").value("Alice"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("POST /api/reservations — nom vide retourne 400")
    void postReservation_nomVide_retourne400() throws Exception {
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "roomId", 1,
                                "personName", "",
                                "startDateTime", "2025-06-10T09:00:00",
                                "endDateTime", "2025-06-10T11:00:00"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/reservations/{id} — retourne 404 si introuvable")
    void getReservation_introuvable_retourne404() throws Exception {
        when(reservationService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Réservation introuvable : id=99"));

        mockMvc.perform(get("/api/reservations/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/reservations/{id}/cancel — retourne 409 si déjà annulée")
    void cancelReservation_dejaAnnulee_retourne409() throws Exception {
        when(reservationService.cancel(1L))
                .thenThrow(new BusinessConflictException("La réservation est déjà annulée"));

        mockMvc.perform(patch("/api/reservations/1/cancel"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PATCH /api/reservations/{id}/cancel — retourne 404 si introuvable")
    void cancelReservation_introuvable_retourne404() throws Exception {
        when(reservationService.cancel(99L))
                .thenThrow(new ResourceNotFoundException("Réservation introuvable : id=99"));

        mockMvc.perform(patch("/api/reservations/99/cancel"))
                .andExpect(status().isNotFound());
    }
}