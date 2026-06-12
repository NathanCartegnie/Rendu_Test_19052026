package org.example.ticketsupport.service;

import org.example.ticketsupport.exception.InvalidStatusTransitionException;
import org.example.ticketsupport.exception.TicketNotFoundException;
import org.example.ticketsupport.model.Priority;
import org.example.ticketsupport.model.Ticket;
import org.example.ticketsupport.model.TicketStatus;
import org.example.ticketsupport.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketService(ticketRepository);
    }

    @Test
    @DisplayName("La création d'un ticket sauvegarde un ticket avec les bonnes informations")
    void creation_dun_ticket_sauvegarde_le_ticket() {
        // Arrange
        String title = "Imprimante hors service";
        Priority priority = Priority.HIGH;

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        // Act
        Ticket result = ticketService.createTicket(title, priority);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getPriority()).isEqualTo(priority);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Un nouveau ticket a le statut OPEN par défaut")
    void nouveau_ticket_a_le_statut_open_par_defaut() {
        // Arrange
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            t.setId(42L);
            return t;
        });

        // Act
        Ticket result = ticketService.createTicket("Ecran qui clignote", Priority.MEDIUM);

        // Assert
        assertThat(result.getStatus()).isEqualTo(TicketStatus.OPEN);
    }

    @Test
    @DisplayName("La recherche d'un ticket existant retourne ce ticket")
    void recherche_dun_ticket_existant_retourne_le_ticket() {
        // Arrange
        Ticket existing = new Ticket(1L, "Problème réseau", Priority.HIGH, TicketStatus.OPEN);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(existing));

        // Act
        Ticket result = ticketService.getTicketById(1L);

        // Assert
        assertThat(result).isEqualTo(existing);
    }

    @Test
    @DisplayName("La recherche d'un ticket inexistant lève une TicketNotFoundException")
    void recherche_dun_ticket_inexistant_leve_une_exception() {
        // Arrange
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> ticketService.getTicketById(99L))
                .isInstanceOf(TicketNotFoundException.class);
    }

    @Test
    @DisplayName("La liste de tous les tickets est retournée correctement")
    void liste_de_tous_les_tickets_est_retournee() {
        // Arrange
        Ticket t1 = new Ticket(1L, "Ticket 1", Priority.LOW, TicketStatus.OPEN);
        Ticket t2 = new Ticket(2L, "Ticket 2", Priority.HIGH, TicketStatus.IN_PROGRESS);
        when(ticketRepository.findAll()).thenReturn(List.of(t1, t2));

        // Act
        List<Ticket> result = ticketService.getAllTickets();

        // Assert
        assertThat(result).containsExactly(t1, t2);
    }

    @Test
    @DisplayName("La transition OPEN vers IN_PROGRESS est autorisée")
    void transition_open_vers_in_progress_est_autorisee() {
        // Arrange
        Ticket ticket = new Ticket(1L, "Ticket", Priority.LOW, TicketStatus.OPEN);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Ticket result = ticketService.updateStatus(1L, TicketStatus.IN_PROGRESS);

        // Assert
        assertThat(result.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("La transition OPEN vers RESOLVED est autorisée")
    void transition_open_vers_resolved_est_autorisee() {
        // Arrange
        Ticket ticket = new Ticket(1L, "Ticket", Priority.LOW, TicketStatus.OPEN);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Ticket result = ticketService.updateStatus(1L, TicketStatus.RESOLVED);

        // Assert
        assertThat(result.getStatus()).isEqualTo(TicketStatus.RESOLVED);
    }

    @Test
    @DisplayName("La transition IN_PROGRESS vers RESOLVED est autorisée")
    void transition_in_progress_vers_resolved_est_autorisee() {
        // Arrange
        Ticket ticket = new Ticket(1L, "Ticket", Priority.LOW, TicketStatus.IN_PROGRESS);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Ticket result = ticketService.updateStatus(1L, TicketStatus.RESOLVED);

        // Assert
        assertThat(result.getStatus()).isEqualTo(TicketStatus.RESOLVED);
    }

    @Test
    @DisplayName("La transition IN_PROGRESS vers OPEN est refusée")
    void transition_in_progress_vers_open_est_refusee() {
        // Arrange
        Ticket ticket = new Ticket(1L, "Ticket", Priority.LOW, TicketStatus.IN_PROGRESS);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // Act / Assert
        assertThatThrownBy(() -> ticketService.updateStatus(1L, TicketStatus.OPEN))
                .isInstanceOf(InvalidStatusTransitionException.class);

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Un ticket déjà RESOLVED ne peut plus changer de statut")
    void ticket_resolved_ne_peut_plus_changer_de_statut() {
        // Arrange
        Ticket ticket = new Ticket(1L, "Ticket", Priority.LOW, TicketStatus.RESOLVED);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // Act / Assert
        assertThatThrownBy(() -> ticketService.updateStatus(1L, TicketStatus.IN_PROGRESS))
                .isInstanceOf(InvalidStatusTransitionException.class);

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("La mise à jour du statut d'un ticket inexistant lève une TicketNotFoundException")
    void maj_statut_ticket_inexistant_leve_une_exception() {
        // Arrange
        when(ticketRepository.findById(404L)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> ticketService.updateStatus(404L, TicketStatus.IN_PROGRESS))
                .isInstanceOf(TicketNotFoundException.class);
    }
}