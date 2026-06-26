package org.example.meeting.repository;

import org.example.meeting.model.Room;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class RoomRepository {

    private final List<Room> rooms = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Room save(Room room) {
        room.setId(idCounter.getAndIncrement());
        rooms.add(room);
        return room;
    }

    public List<Room> findAll() {
        return List.copyOf(rooms);
    }

    public Optional<Room> findById(Long id) {
        return rooms.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public void clear() {
        rooms.clear();
        idCounter.set(1);
    }
}