package com.nmit.canteen.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private MenuItem menuItem;
    private int quantity;
    private double price; // unit price at time of order
    @ManyToOne @JsonIgnore
    private Order order;

    public OrderItem() {}
    public OrderItem(Order order, MenuItem menuItem, int quantity) {
        this.order = order; this.menuItem = menuItem; this.quantity = quantity; this.price = menuItem.getPrice();
    }
    public Long getId() { return id; }
    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public Order getOrder() { return order; }
}
