package org.example.meeting.service;

import org.example.meeting.dto.RoomCreateRequest;
import org.example.meeting.model.Room;
import org.example.meeting.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public Room create(RoomCreateRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Le nom de la salle est obligatoire");
        }
        if (request.getCapacity() < 1) {
            throw new IllegalArgumentException("La capacité doit être supérieure ou égale à 1");
        }
        Room room = new Room(null, request.getName(), request.getCapacity());
        return repository.save(room);
    }

    public List<Room> findAll() {
        return repository.findAll();
    }

    public Room findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new org.example.meeting.exception.ResourceNotFoundException(
                        "Salle introuvable : id=" + id));
    }
}