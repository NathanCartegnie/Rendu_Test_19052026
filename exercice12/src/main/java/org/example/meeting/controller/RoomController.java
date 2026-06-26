package org.example.meeting.controller;

import jakarta.validation.Valid;
import org.example.meeting.dto.RoomCreateRequest;
import org.example.meeting.model.Room;
import org.example.meeting.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    /**
     * POST /api/rooms — Crée une nouvelle salle.
     * Retourne 201 Created avec la salle créée.
     */
    @PostMapping
    public ResponseEntity<Room> createRoom(@Valid @RequestBody RoomCreateRequest request) {
        Room room = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    /**
     * GET /api/rooms — Liste toutes les salles.
     */
    @GetMapping
    public ResponseEntity<List<Room>> listRooms() {
        return ResponseEntity.ok(service.findAll());
    }
}
