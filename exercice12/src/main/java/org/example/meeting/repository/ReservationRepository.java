package org.example.meeting.repository;

import org.example.meeting.model.Reservation;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ReservationRepository {

    private final List<Reservation> reservations = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Reservation save(Reservation reservation) {
        reservation.setId(idCounter.getAndIncrement());
        reservations.add(reservation);
        return reservation;
    }

    public Optional<Reservation> findById(Long id) {
        return reservations.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public List<Reservation> findAll() {
        return List.copyOf(reservations);
    }

    public void clear() {
        reservations.clear();
        idCounter.set(1);
    }
}