package models;

import java.util.List;

public class Master {
    private String id;
    private String name;
    private String photoURL;
    private List<ScheduleItem> schedule;
    // — Тепер це масив, а не простий рядок:
    private List<String> specializations;
    private String category;  // за потреби
    private String userId;

    public Master() {}

    public Master(String name,
                  String photoURL,
                  List<ScheduleItem> schedule,
                  List<String> specializations,
                  String category,
                  String userId) {
        this.name = name;
        this.photoURL = photoURL;
        this.schedule = schedule;
        this.specializations = specializations;
        this.category = category;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoURL() {
        return photoURL;
    }
    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public List<ScheduleItem> getSchedule() {
        return schedule;
    }
    public void setSchedule(List<ScheduleItem> schedule) {
        this.schedule = schedule;
    }

    // Оновлені геттери/сеттери для масиву:
    public List<String> getSpecializations() {
        return specializations;
    }
    public void setSpecializations(List<String> specializations) {
        this.specializations = specializations;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
