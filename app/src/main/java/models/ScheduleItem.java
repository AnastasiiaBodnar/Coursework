package models;

public class ScheduleItem {
    private String day;
    private String start;
    private String end;

    public ScheduleItem() {}

    public ScheduleItem(String day, String start, String end) {
        this.day = day;
        this.start = start;
        this.end = end;
    }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }
    public String getEnd() { return end; }
    public void setEnd(String end) { this.end = end; }
}