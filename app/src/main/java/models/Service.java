package models;

public class Service {
    private String id;
    private String category;
    private String name;
    private String price;
    private String description;

    public Service() {}

    public Service(String category, String name, String price, String description) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}