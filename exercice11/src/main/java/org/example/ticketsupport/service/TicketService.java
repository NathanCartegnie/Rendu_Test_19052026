package org.example.ticketsupport.service;

import org.example.ticketsupport.exception.InvalidStatusTransitionException;
import org.example.ticketsupport.exception.TicketNotFoundException;
import org.example.ticketsupport.model.Priority;
import org.example.ticketsupport.model.Ticket;
import org.example.ticketsupport.model.TicketStatus;
import org.example.ticketsupport.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TicketService {

    /**
     * Table des transitions de statut autorisées.
     */
    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED_TRANSITIONS = Map.of(
            TicketStatus.OPEN, Set.of(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED),
            TicketStatus.IN_PROGRESS, Set.of(TicketStatus.RESOLVED),
            TicketStatus.RESOLVED, Set.of()
    );

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Crée un nouveau ticket avec le statut OPEN par défaut.
     */
    public Ticket createTicket(String title, Priority priority) {
        Ticket ticket = new Ticket(null, title, priority, TicketStatus.OPEN);
        return ticketRepository.save(ticket);
    }

    /**
     * Récupère un ticket par son identifiant.
     *
     * @throws TicketNotFoundException si le ticket n'existe pas
     */
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    /**
     * Retourne la liste de tous les tickets.
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Met à jour le statut d'un ticket en respectant les transitions autorisées.
     *
     * @throws TicketNotFoundException        si le ticket n'existe pas
     * @throws InvalidStatusTransitionException si la transition demandée est interdite
     */
    public Ticket updateStatus(Long id, TicketStatus newStatus) {
        Ticket ticket = getTicketById(id);
        TicketStatus currentStatus = ticket.getStatus();

        Set<TicketStatus> allowedTargets = ALLOWED_TRANSITIONS.get(currentStatus);
        if (allowedTargets == null || !allowedTargets.contains(newStatus)) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }

        ticket.setStatus(newStatus);
        return ticketRepository.save(ticket);
    }
}