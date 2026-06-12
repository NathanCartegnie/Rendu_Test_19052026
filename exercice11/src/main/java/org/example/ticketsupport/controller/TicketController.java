package org.example.ticketsupport.controller;

import jakarta.validation.Valid;
import org.example.ticketsupport.dto.TicketCreateRequest;
import org.example.ticketsupport.dto.UpdateStatusRequest;
import org.example.ticketsupport.model.Ticket;
import org.example.ticketsupport.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Crée un nouveau ticket.
     * POST /api/tickets -> 201 Created
     */
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody TicketCreateRequest request) {
        Ticket created = ticketService.createTicket(request.getTitle(), request.getPriority());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Récupère un ticket par son identifiant.
     * GET /api/tickets/{id} -> 200 OK ou 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Ticket ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    /**
     * Liste tous les tickets.
     * GET /api/tickets -> 200 OK
     */
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    /**
     * Modifie le statut d'un ticket.
     * PATCH /api/tickets/{id}/status -> 200 OK, 404 Not Found ou 409 Conflict
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Ticket> updateStatus(@PathVariable Long id,
                                               @Valid @RequestBody UpdateStatusRequest request) {
        Ticket updated = ticketService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(updated);
    }
}