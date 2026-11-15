package com.nmit.canteen.controller;

import com.nmit.canteen.model.MenuItem;
import com.nmit.canteen.repository.MenuItemRepository;
import com.nmit.canteen.service.AuthService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
    private final MenuItemRepository repo;
    private final AuthService auth;

    public MenuController(MenuItemRepository repo, AuthService auth) { this.repo = repo; this.auth = auth; }

    /** GET /api/menu?category=Snacks&veg=true&maxPrice=50 */
    @GetMapping
    public List<MenuItem> list(@RequestParam(required = false) String category,
                               @RequestParam(required = false) Boolean veg,
                               @RequestParam(required = false) Double maxPrice) {
        return repo.findAll().stream()
                .filter(i -> category == null || category.isBlank() || i.getCategory().equalsIgnoreCase(category))
                .filter(i -> veg == null || i.isVeg() == veg)
                .filter(i -> maxPrice == null || i.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MenuItem one(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItem create(@RequestHeader(value = "Authorization", required = false) String h, @RequestBody MenuItem item) {
        auth.requireAdmin(h);
        return repo.save(item);
    }

    @PutMapping("/{id}")
    public MenuItem update(@RequestHeader(value = "Authorization", required = false) String h,
                           @PathVariable Long id, @RequestBody MenuItem in) {
        auth.requireAdmin(h);
        MenuItem m = one(id);
        m.setName(in.getName()); m.setCategory(in.getCategory()); m.setPrice(in.getPrice());
        m.setVeg(in.isVeg()); m.setSpicy(in.isSpicy()); m.setAvailable(in.isAvailable()); m.setDescription(in.getDescription());
        return repo.save(m);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(value = "Authorization", required = false) String h, @PathVariable Long id) {
        auth.requireAdmin(h);
        repo.deleteById(id);
    }
}
