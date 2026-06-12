package org.example.ticketsupport.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ticketsupport.dto.TicketCreateRequest;
import org.example.ticketsupport.dto.UpdateStatusRequest;
import org.example.ticketsupport.exception.InvalidStatusTransitionException;
import org.example.ticketsupport.exception.TicketNotFoundException;
import org.example.ticketsupport.model.Priority;
import org.example.ticketsupport.model.Ticket;
import org.example.ticketsupport.model.TicketStatus;
import org.example.ticketsupport.service.TicketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    @Test
    @DisplayName("POST /api/tickets avec des données valides retourne 201 et le ticket créé")
    void creation_ticket_valide_retourne_201() throws Exception {
        // Arrange
        TicketCreateRequest request = new TicketCreateRequest("Imprimante en panne", Priority.HIGH);
        Ticket created = new Ticket(1L, "Imprimante en panne", Priority.HIGH, TicketStatus.OPEN);

        when(ticketService.createTicket("Imprimante en panne", Priority.HIGH)).thenReturn(created);

        // Act / Assert
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Imprimante en panne"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @DisplayName("POST /api/tickets avec un titre trop court retourne 400")
    void creation_ticket_titre_trop_court_retourne_400() throws Exception {
        // Arrange
        TicketCreateRequest request = new TicketCreateRequest("ab", Priority.LOW);

        // Act / Assert
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(ticketService, never()).createTicket(any(), any());
    }

    @Test
    @DisplayName("POST /api/tickets sans priorité retourne 400")
    void creation_ticket_sans_priorite_retourne_400() throws Exception {
        // Arrange
        String body = "{\"title\": \"Probleme reseau\"}";

        // Act / Assert
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(ticketService, never()).createTicket(any(), any());
    }

    @Test
    @DisplayName("GET /api/tickets/{id} sur un ticket existant retourne 200 et le ticket")
    void recuperation_ticket_existant_retourne_200() throws Exception {
        // Arrange
        Ticket ticket = new Ticket(1L, "Ecran noir", Priority.MEDIUM, TicketStatus.OPEN);
        when(ticketService.getTicketById(1L)).thenReturn(ticket);

        // Act / Assert
        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Ecran noir"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @DisplayName("GET /api/tickets/{id} sur un ticket inexistant retourne 404")
    void recuperation_ticket_inexistant_retourne_404() throws Exception {
        // Arrange
        when(ticketService.getTicketById(99L)).thenThrow(new TicketNotFoundException(99L));

        // Act / Assert
        mockMvc.perform(get("/api/tickets/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tickets retourne la liste de tous les tickets")
    void liste_tickets_retourne_200_et_la_liste() throws Exception {
        // Arrange
        Ticket t1 = new Ticket(1L, "Ticket 1", Priority.LOW, TicketStatus.OPEN);
        Ticket t2 = new Ticket(2L, "Ticket 2", Priority.HIGH, TicketStatus.IN_PROGRESS);
        when(ticketService.getAllTickets()).thenReturn(List.of(t1, t2));

        // Act / Assert
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("PATCH /api/tickets/{id}/status avec une transition valide retourne 200")
    void maj_statut_transition_valide_retourne_200() throws Exception {
        // Arrange
        UpdateStatusRequest request = new UpdateStatusRequest(TicketStatus.IN_PROGRESS);
        Ticket updated = new Ticket(1L, "Ticket", Priority.LOW, TicketStatus.IN_PROGRESS);

        when(ticketService.updateStatus(1L, TicketStatus.IN_PROGRESS)).thenReturn(updated);

        // Act / Assert
        mockMvc.perform(patch("/api/tickets/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("PATCH /api/tickets/{id}/status sur un ticket inexistant retourne 404")
    void maj_statut_ticket_inexistant_retourne_404() throws Exception {
        // Arrange
        UpdateStatusRequest request = new UpdateStatusRequest(TicketStatus.IN_PROGRESS);

        when(ticketService.updateStatus(99L, TicketStatus.IN_PROGRESS))
                .thenThrow(new TicketNotFoundException(99L));

        // Act / Assert
        mockMvc.perform(patch("/api/tickets/99/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/tickets/{id}/status avec une transition interdite retourne 409")
    void maj_statut_transition_interdite_retourne_409() throws Exception {
        // Arrange
        UpdateStatusRequest request = new UpdateStatusRequest(TicketStatus.OPEN);

        when(ticketService.updateStatus(1L, TicketStatus.OPEN))
                .thenThrow(new InvalidStatusTransitionException(TicketStatus.RESOLVED, TicketStatus.OPEN));

        // Act / Assert
        mockMvc.perform(patch("/api/tickets/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}