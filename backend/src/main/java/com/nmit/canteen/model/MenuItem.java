package com.nmit.canteen.model;

import jakarta.persistence.*;

@Entity
public class MenuItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private double price;
    private boolean veg;
    private boolean spicy;
    private boolean available = true;
    @Column(length = 300)
    private String description;

    public MenuItem() {}
    public MenuItem(String name, String category, double price, boolean veg, boolean spicy, String description) {
        this.name = name; this.category = category; this.price = price;
        this.veg = veg; this.spicy = spicy; this.description = description;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public boolean isVeg() { return veg; }
    public void setVeg(boolean veg) { this.veg = veg; }
    public boolean isSpicy() { return spicy; }
    public void setSpicy(boolean spicy) { this.spicy = spicy; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
