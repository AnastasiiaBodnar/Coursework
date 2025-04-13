package models;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class WorkingDay {
    private int id;
    private Master master;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isDayOff;

    public WorkingDay() {
    }

    public WorkingDay(int id, Master master, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, boolean isDayOff) {
        this.id = id;
        this.master = master;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isDayOff = isDayOff;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        this.master = master;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isDayOff() {
        return isDayOff;
    }

    public void setDayOff(boolean dayOff) {
        isDayOff = dayOff;
    }

    @Override
    public String toString() {
        return "WorkingDay{" +
                "id=" + id +
                ", master=" + master +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isDayOff=" + isDayOff +
                '}';
    }
}