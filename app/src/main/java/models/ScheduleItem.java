package models;

import java.util.List;

public class ScheduleItem {
    private String day;
    private List<String> slots;

    public ScheduleItem() {}

    public ScheduleItem(String day, List<String> slots) {
        this.day = day;
        this.slots = slots;
    }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public List<String> getSlots() { return slots; }
    public void setSlots(List<String> slots) { this.slots = slots; }
}
