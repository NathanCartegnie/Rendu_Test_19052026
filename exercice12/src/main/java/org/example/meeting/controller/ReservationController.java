package org.example.meeting.controller;

import jakarta.validation.Valid;
import org.example.meeting.dto.ReservationCreateRequest;
import org.example.meeting.model.Reservation;
import org.example.meeting.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @Valid @RequestBody ReservationCreateRequest request) {
        Reservation created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/reservations/{id} — Consulte une réservation.
     * Retourne 404 via GlobalExceptionHandler si introuvable.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservation(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * PATCH /api/reservations/{id}/cancel — Annule une réservation.
     * Retourne 409 via GlobalExceptionHandler si déjà annulée.
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Reservation> cancelReservation(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.cancel(id));
    }
}


