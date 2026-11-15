package com.nmit.canteen.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference;
    @ManyToOne
    private User user;
    private String status = "PLACED"; // PLACED, PREPARING, READY, COMPLETED, CANCELLED
    private String pickupSlot;
    private double subtotal;
    private double discount;
    private double tax;
    private double total;
    private LocalDateTime createdAt = LocalDateTime.now();
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    public Long getId() { return id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPickupSlot() { return pickupSlot; }
    public void setPickupSlot(String pickupSlot) { this.pickupSlot = pickupSlot; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }
}
