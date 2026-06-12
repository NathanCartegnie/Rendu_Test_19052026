package org.example.ticketsupport.exception;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(Long id) {
        super("Le ticket avec l'identifiant " + id + " n'existe pas");
    }
}