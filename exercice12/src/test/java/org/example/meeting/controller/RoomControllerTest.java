package org.example.meeting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.meeting.exception.ResourceNotFoundException;
import org.example.meeting.model.Room;
import org.example.meeting.service.RoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomService roomService;

    @Test
    @DisplayName("POST /api/rooms — création correcte retourne 201 avec la salle")
    void postRoom_donnéesValides_retourne201() throws Exception {
        Room salle = new Room(1L, "Salle Apollo", 10);
        when(roomService.create(any())).thenReturn(salle);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("name", "Salle Apollo", "capacity", 10))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Salle Apollo"));
    }

    @Test
    @DisplayName("POST /api/rooms — nom vide retourne 400")
    void postRoom_nomVide_retourne400() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("name", "", "capacity", 10))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/rooms — capacité à 0 retourne 400")
    void postRoom_capaciteZero_retourne400() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("name", "Salle B", "capacity", 0))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/rooms — retourne la liste des salles")
    void getRooms_retourneListeSalles() throws Exception {
        when(roomService.findAll()).thenReturn(List.of(
                new Room(1L, "Salle Apollo", 10),
                new Room(2L, "Salle Gemini", 5)
        ));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/rooms — retourne 404 si la salle demandée est introuvable")
    void getRoom_introuvable_retourne404() throws Exception {
        when(roomService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Salle introuvable : id=99"));

        mockMvc.perform(get("/api/rooms/99"))
                .andExpect(status().isNotFound());
    }
}