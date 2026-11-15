package com.nmit.canteen.controller;

import com.nmit.canteen.model.Order;
import com.nmit.canteen.service.AuthService;
import com.nmit.canteen.service.OrderService;
import com.nmit.canteen.service.OrderService.ItemRequest;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    public record PlaceOrderRequest(String pickupSlot, List<ItemRequest> items) {}

    private final OrderService service;
    private final AuthService auth;

    public OrderController(OrderService service, AuthService auth) { this.service = service; this.auth = auth; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order place(@RequestHeader(value = "Authorization", required = false) String h, @RequestBody PlaceOrderRequest r) {
        return service.place(auth.requireUser(h), r.pickupSlot(), r.items());
    }

    @GetMapping("/my")
    public List<Order> mine(@RequestHeader(value = "Authorization", required = false) String h) {
        return service.forUser(auth.requireUser(h));
    }

    @GetMapping
    public List<Order> all(@RequestHeader(value = "Authorization", required = false) String h) {
        auth.requireAdmin(h);
        return service.all();
    }

    @PutMapping("/{id}/status")
    public Order status(@RequestHeader(value = "Authorization", required = false) String h,
                        @PathVariable Long id, @RequestParam String status) {
        auth.requireAdmin(h);
        return service.updateStatus(id, status);
    }

    @GetMapping("/stats")
    public Map<String, Object> stats(@RequestHeader(value = "Authorization", required = false) String h) {
        auth.requireAdmin(h);
        return service.stats();
    }
}
