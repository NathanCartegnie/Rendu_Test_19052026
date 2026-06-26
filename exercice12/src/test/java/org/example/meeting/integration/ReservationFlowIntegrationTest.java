package org.example.meeting.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.meeting.repository.ReservationRepository;
import org.example.meeting.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void reinitialiserRepositories() {
        reservationRepository.clear();
        roomRepository.clear();
    }

    @Test
    @DisplayName("Parcours complet : créer salle → réserver → consulter → annuler")
    void parcours_complet_creerSalleReserverConsulterAnnuler() throws Exception {

        MvcResult resultatSalle = mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Salle Intégration", "capacity": 8}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode salle = objectMapper.readTree(resultatSalle.getResponse().getContentAsString());
        long salleId = salle.get("id").asLong();

        MvcResult resultatReservation = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                  "roomId": %d,
                                  "personName": "Nathan",
                                  "startDateTime": "2025-09-01T10:00:00",
                                  "endDateTime": "2025-09-01T12:00:00"
                                }
                                """, salleId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andReturn();

        JsonNode reservation = objectMapper.readTree(
                resultatReservation.getResponse().getContentAsString());
        long reservationId = reservation.get("id").asLong();

        mockMvc.perform(get("/api/reservations/" + reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personName").value("Nathan"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        MvcResult resultatAnnulation = mockMvc.perform(
                        patch("/api/reservations/" + reservationId + "/cancel"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode annulee = objectMapper.readTree(
                resultatAnnulation.getResponse().getContentAsString());
        assertThat(annulee.get("status").asText()).isEqualTo("CANCELLED");
    }

    @Test
    @DisplayName("Parcours d'erreur : réserver sur un créneau déjà pris retourne 409")
    void parcours_conflitCreneau_retourne409() throws Exception {

        MvcResult resultatSalle = mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Salle Conflit", "capacity": 5}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        long salleId = objectMapper.readTree(
                resultatSalle.getResponse().getContentAsString()).get("id").asLong();

        String bodyPremier = String.format("""
                {
                  "roomId": %d,
                  "personName": "Alice",
                  "startDateTime": "2025-09-02T09:00:00",
                  "endDateTime": "2025-09-02T11:00:00"
                }
                """, salleId);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyPremier))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                  "roomId": %d,
                                  "personName": "Bob",
                                  "startDateTime": "2025-09-02T10:00:00",
                                  "endDateTime": "2025-09-02T12:00:00"
                                }
                                """, salleId)))
                .andExpect(status().isConflict());
    }
}