package models;

import java.util.List;

public class Master {
    private String id;
    private String name;
    private String photoURL;
    private List<ScheduleItem> schedule;
    private String category;
    private String userId;
    private String specialization;

    public Master() {}

    public Master(String name, String photoURL, List<ScheduleItem> schedule, String category, String specialization, String userId) {
        this.name = name;
        this.photoURL = photoURL;
        this.schedule = schedule;
        this.category = category;
        this.specialization = specialization;
        this.userId = userId;
    }

    public String getId() {return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
    public String getPhotoURL() { return photoURL; }
    public void setPhotoURL(String photoURL) { this.photoURL = photoURL; }
    public List<ScheduleItem> getSchedule() { return schedule; }
    public void setSchedule(List<ScheduleItem> schedule) { this.schedule = schedule; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSpecialization() { return specialization; }

}
