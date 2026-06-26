package org.example.meeting.service;

import org.example.meeting.dto.ReservationCreateRequest;
import org.example.meeting.exception.BusinessConflictException;
import org.example.meeting.exception.ResourceNotFoundException;
import org.example.meeting.model.Reservation;
import org.example.meeting.repository.ReservationRepository;
import org.example.meeting.repository.RoomRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    public Reservation create(ReservationCreateRequest request) {
        roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Salle introuvable : id=" + request.getRoomId()));

        if (request.getPersonName() == null || request.getPersonName().isBlank()) {
            throw new IllegalArgumentException("Le nom de la personne est obligatoire");
        }

        if (request.getEndDateTime() == null || request.getStartDateTime() == null
                || !request.getEndDateTime().isAfter(request.getStartDateTime())) {
            throw new IllegalArgumentException(
                    "La date de fin doit être strictement après la date de début");
        }

        boolean chevauchement = reservationRepository.findAll().stream()
                .filter(r -> r.getRoomId().equals(request.getRoomId()))
                .filter(r -> r.getStatus() == Reservation.Status.CONFIRMED)
                .anyMatch(r -> overlaps(r, request));

        if (chevauchement) {
            throw new BusinessConflictException(
                    "Le créneau chevauche une réservation existante pour cette salle");
        }

        Reservation reservation = new Reservation(
                null,
                request.getRoomId(),
                request.getPersonName(),
                request.getStartDateTime(),
                request.getEndDateTime()
        );
        return reservationRepository.save(reservation);
    }

    public Reservation getById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réservation introuvable : id=" + id));
    }

    public Reservation cancel(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réservation introuvable : id=" + id));

        if (reservation.getStatus() == Reservation.Status.CANCELLED) {
            throw new BusinessConflictException("La réservation est déjà annulée");
        }

        reservation.setStatus(Reservation.Status.CANCELLED);
        return reservation;
    }

    private boolean overlaps(Reservation existing, ReservationCreateRequest request) {
        return request.getStartDateTime().isBefore(existing.getEndDateTime())
                && request.getEndDateTime().isAfter(existing.getStartDateTime());
    }
}