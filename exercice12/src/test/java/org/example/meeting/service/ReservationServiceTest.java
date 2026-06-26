package org.example.meeting.service;

import org.example.meeting.dto.ReservationCreateRequest;
import org.example.meeting.exception.BusinessConflictException;
import org.example.meeting.exception.ResourceNotFoundException;
import org.example.meeting.model.*;
import org.example.meeting.repository.ReservationRepository;
import org.example.meeting.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private ReservationService service;

    private Room salleExistante;
    private LocalDateTime debut;
    private LocalDateTime fin;

    @BeforeEach
    void setUp() {
        salleExistante = new Room(1L, "Salle Apollo", 10);
        debut = LocalDateTime.of(2025, 6, 10, 9, 0);
        fin   = LocalDateTime.of(2025, 6, 10, 11, 0);
    }

    @Test
    @DisplayName("Création d'une réservation valide — retourne la réservation confirmée")
    void creation_reservationValide_retourneReservationConfirmee() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(salleExistante));
        when(reservationRepository.findAll()).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        Reservation result = service.create(requete("Alice", 1L, debut, fin));

        assertThat(result.getStatus()).isEqualTo(Reservation.Status.CONFIRMED);
        assertThat(result.getPersonName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Création refusée — la salle n'existe pas")
    void creation_salleInexistante_leveResourceNotFoundException() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(requete("Alice", 99L, debut, fin)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Salle introuvable");
    }

    @Test
    @DisplayName("Création refusée — le créneau est invalide (fin avant début)")
    void creation_creneauInvalide_leveIllegalArgumentException() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(salleExistante));

        assertThatThrownBy(() -> service.create(requete("Alice", 1L, fin, debut)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("date de fin");
    }

    @Test
    @DisplayName("Création refusée — chevauchement avec une réservation confirmée existante")
    void creation_chevauchement_leveBusinessConflictException() {
        Reservation existante = reservationConfirmee(1L, debut, fin);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(salleExistante));
        when(reservationRepository.findAll()).thenReturn(List.of(existante));

        LocalDateTime debut2 = LocalDateTime.of(2025, 6, 10, 10, 0);
        LocalDateTime fin2   = LocalDateTime.of(2025, 6, 10, 12, 0);

        assertThatThrownBy(() -> service.create(requete("Bob", 1L, debut2, fin2)))
                .isInstanceOf(BusinessConflictException.class)
                .hasMessageContaining("chevauche");
    }

    @Test
    @DisplayName("Création autorisée — réservation annulée ne bloque pas le créneau")
    void creation_reservationAnnuleeExistante_pasDeConflitAttendu() {
        Reservation annulee = reservationConfirmee(1L, debut, fin);
        annulee.setStatus(Reservation.Status.CANCELLED);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(salleExistante));
        when(reservationRepository.findAll()).thenReturn(List.of(annulee));
        when(reservationRepository.save(any())).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(2L);
            return r;
        });

        Reservation result = service.create(requete("Bob", 1L, debut, fin));
        assertThat(result.getStatus()).isEqualTo(Reservation.Status.CONFIRMED);
    }

    @Test
    @DisplayName("Annulation d'une réservation confirmée — statut passe à CANCELLED")
    void annulation_reservationConfirmee_passeCancelled() {
        Reservation reservation = reservationConfirmee(1L, debut, fin);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation result = service.cancel(1L);

        assertThat(result.getStatus()).isEqualTo(Reservation.Status.CANCELLED);
    }

    @Test
    @DisplayName("Annulation refusée — la réservation est déjà annulée")
    void annulation_reservationDejaAnnulee_leveBusinessConflictException() {
        Reservation reservation = reservationConfirmee(1L, debut, fin);
        reservation.setStatus(Reservation.Status.CANCELLED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.cancel(1L))
                .isInstanceOf(BusinessConflictException.class)
                .hasMessageContaining("déjà annulée");
    }

    @Test
    @DisplayName("Annulation refusée — la réservation n'existe pas")
    void annulation_reservationInexistante_leveResourceNotFoundException() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private ReservationCreateRequest requete(String nom, Long roomId,
                                             LocalDateTime debut, LocalDateTime fin) {
        ReservationCreateRequest req = new ReservationCreateRequest();
        req.setPersonName(nom);
        req.setRoomId(roomId);
        req.setStartDateTime(debut);
        req.setEndDateTime(fin);
        return req;
    }

    private Reservation reservationConfirmee(Long roomId,
                                             LocalDateTime debut, LocalDateTime fin) {
        Reservation r = new Reservation(1L, roomId, "Alice", debut, fin);
        r.setStatus(Reservation.Status.CONFIRMED);
        return r;
    }
}