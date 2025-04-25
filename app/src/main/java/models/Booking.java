package models;

public class Booking {
    private String serviceName;
    private String date;
    private String time;
    private String masterName;
    private String status;

    public Booking() {}

    public Booking(String serviceName, String date, String time, String masterName, String status) {
        this.serviceName = serviceName;
        this.date = date;
        this.time = time;
        this.masterName = masterName;
        this.status = status;
    }

    public String getServiceName() { return serviceName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getMasterName() { return masterName; }
    public String getStatus() { return status; }
}
