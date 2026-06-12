package org.example.ticketsupport.dto;

import jakarta.validation.constraints.NotNull;
import org.example.ticketsupport.model.TicketStatus;

public class UpdateStatusRequest {

    @NotNull(message = "Le statut est obligatoire")
    private TicketStatus status;

    public UpdateStatusRequest() {
    }

    public UpdateStatusRequest(TicketStatus status) {
        this.status = status;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}