package org.example.ticketsupport.exception;

import org.example.ticketsupport.model.TicketStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(TicketStatus from, TicketStatus to) {
        super("La transition de " + from + " vers " + to + " n'est pas autorisée");
    }
}