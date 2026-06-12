package org.example.ticketsupport.repository;

import org.example.ticketsupport.model.Ticket;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Stockage en mémoire des tickets de support.
 */
@Repository
public class TicketRepository {

    private final Map<Long, Ticket> tickets = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * Sauvegarde un ticket. Si le ticket n'a pas d'identifiant,
     * un nouvel identifiant lui est attribué.
     */
    public Ticket save(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(idGenerator.incrementAndGet());
        }
        tickets.put(ticket.getId(), ticket);
        return ticket;
    }

    /**
     * Recherche un ticket par son identifiant.
     */
    public Optional<Ticket> findById(Long id) {
        return Optional.ofNullable(tickets.get(id));
    }

    /**
     * Retourne tous les tickets.
     */
    public List<Ticket> findAll() {
        return List.copyOf(tickets.values());
    }

    /**
     * Supprime tous les tickets (utile pour les tests).
     */
    public void deleteAll() {
        tickets.clear();
        idGenerator.set(0);
    }
}