package models;

public class Service {
    private String id;
    private String category;
    private String subcategory; // Додано поле для підкатегорії
    private String name;
    private String price;
    private String description;

    // Пустий конструктор (обов'язковий для Firestore)
    public Service() {}

    // Конструктор з усіма полями
    public Service(String category, String subcategory, String name, String price, String description) {
        this.category = category;
        this.subcategory = subcategory;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    // Гетери та сетери для всіх полів
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubcategory() { return subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Опціонально: метод toString() для зручності логування
    @Override
    public String toString() {
        return "Service{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", subcategory='" + subcategory + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}