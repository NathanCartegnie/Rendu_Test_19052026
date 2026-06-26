package org.example.meeting.model;

import java.time.LocalDateTime;

public class Reservation {

    public enum Status {
        CONFIRMED,
        CANCELLED
    }

    private Long id;
    private Long roomId;
    private String personName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Status status;

    public Reservation() {}

    public Reservation(Long id, Long roomId, String personName,
                       LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.id = id;
        this.roomId = roomId;
        this.personName = personName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = Status.CONFIRMED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}