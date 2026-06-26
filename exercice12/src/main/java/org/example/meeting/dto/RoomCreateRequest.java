package org.example.meeting.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RoomCreateRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @Min(value = 1, message = "La capacité doit être supérieure ou égale à 1")
    private int capacity;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}