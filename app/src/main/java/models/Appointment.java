package models;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private User client;
    private Master master;
    private Service service;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // "scheduled", "completed", "canceled"
    private String notes;

    public Appointment() {
    }

    public Appointment(int id, User client, Master master, Service service, LocalDateTime startTime, LocalDateTime endTime, String status, String notes) {
        this.id = id;
        this.client = client;
        this.master = master;
        this.service = service;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        this.master = master;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", client=" + client +
                ", master=" + master +
                ", service=" + service +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}