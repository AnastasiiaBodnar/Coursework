package models;

public class Booking {
    private String id;
    private String serviceName;
    private String date;
    private String time;
    private String masterName;
    public Booking() {}

    public Booking(String id, String serviceName, String date, String time, String masterName) {
        this.id = id;
        this.serviceName = serviceName;
        this.date = date;
        this.time = time;
        this.masterName = masterName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getServiceName() { return serviceName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getMasterName() { return masterName; }
}
