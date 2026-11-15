package com.nmit.canteen.service;

import com.nmit.canteen.model.*;
import com.nmit.canteen.repository.MenuItemRepository;
import com.nmit.canteen.repository.OrderRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {
    public record ItemRequest(Long menuItemId, int quantity) {}

    private static final Set<String> STATUSES = Set.of("PLACED", "PREPARING", "READY", "COMPLETED", "CANCELLED");

    private final OrderRepository orders;
    private final MenuItemRepository menu;
    private final PricingService pricing;                     // constructor injection
    private final ObjectProvider<OrderRefGenerator> refs;     // gives a fresh prototype bean each call

    public OrderService(OrderRepository orders, MenuItemRepository menu, PricingService pricing,
                        ObjectProvider<OrderRefGenerator> refs) {
        this.orders = orders; this.menu = menu; this.pricing = pricing; this.refs = refs;
    }

    @Transactional
    public Order place(User user, String slot, List<ItemRequest> items) {
        if (items == null || items.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        if (slot == null || slot.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pick a pickup slot");

        Order o = new Order();
        o.setUser(user);
        o.setPickupSlot(slot);
        o.setReference(refs.getObject().next());

        double subtotal = 0;
        for (ItemRequest r : items) {
            if (r.quantity() < 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1");
            MenuItem m = menu.findById(r.menuItemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
            if (!m.isAvailable())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, m.getName() + " is unavailable");
            o.getItems().add(new OrderItem(o, m, r.quantity()));
            subtotal += m.getPrice() * r.quantity();
        }
        double discount = pricing.discount(subtotal);
        double tax = pricing.tax(subtotal - discount);
        o.setSubtotal(subtotal);
        o.setDiscount(discount);
        o.setTax(tax);
        o.setTotal(subtotal - discount + tax);
        return orders.save(o);
    }

    public List<Order> forUser(User u) { return orders.findByUserOrderByCreatedAtDesc(u); }

    public List<Order> all() { return orders.findAllByOrderByCreatedAtDesc(); }

    @Transactional
    public Order updateStatus(Long id, String status) {
        if (!STATUSES.contains(status))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        Order o = orders.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        o.setStatus(status);
        return orders.save(o);
    }

    public Map<String, Object> stats() {
        List<Order> all = orders.findAll();
        double sales = all.stream().filter(o -> !"CANCELLED".equals(o.getStatus())).mapToDouble(Order::getTotal).sum();
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (String s : List.of("PLACED", "PREPARING", "READY", "COMPLETED", "CANCELLED"))
            byStatus.put(s, all.stream().filter(o -> s.equals(o.getStatus())).count());
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalOrders", all.size());
        out.put("totalSales", Math.round(sales * 100.0) / 100.0);
        out.put("byStatus", byStatus);
        return out;
    }
}
