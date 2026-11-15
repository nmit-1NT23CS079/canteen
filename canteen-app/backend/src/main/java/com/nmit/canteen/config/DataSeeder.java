package com.nmit.canteen.config;

import com.nmit.canteen.model.MenuItem;
import com.nmit.canteen.model.User;
import com.nmit.canteen.repository.MenuItemRepository;
import com.nmit.canteen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Seeds sample menu data and local demo accounts; production admin credentials come from the environment. */
@Component
public class DataSeeder implements CommandLineRunner {
    private final MenuItemRepository menu;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final Environment environment;
    private final String adminPassword;

    public DataSeeder(MenuItemRepository menu, UserRepository users, PasswordEncoder encoder,
                      Environment environment, @Value("${CANTEEN_ADMIN_PASSWORD:}") String adminPassword) {
        this.menu = menu;
        this.users = users;
        this.encoder = encoder;
        this.environment = environment;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        boolean production = environment.acceptsProfiles(Profiles.of("prod"));
        if (users.findByEmail("admin@canteen.com").isEmpty()) {
            String password = production ? adminPassword : "admin123";
            if (password.isBlank()) {
                throw new IllegalStateException("Set CANTEEN_ADMIN_PASSWORD before starting with the prod profile");
            }
            users.save(new User("Canteen Admin", "admin@canteen.com", encoder.encode(password), "ADMIN"));
        }
        if (!production && users.findByEmail("student@canteen.com").isEmpty()) {
            users.save(new User("Demo Student", "student@canteen.com", encoder.encode("student123"), "STUDENT"));
        }

        seedMenuItem("Masala Dosa", "Breakfast", 60, true, true, "Crispy dosa with spicy potato filling");
        seedMenuItem("Idli Vada", "Breakfast", 45, true, false, "Soft idlis with crunchy vada and sambar");
        seedMenuItem("Poori Bhaji", "Breakfast", 50, true, false, "Fluffy poori with potato bhaji");
        seedMenuItem("Veg Fried Rice", "Meals", 90, true, false, "Wok tossed rice with vegetables");
        seedMenuItem("Chicken Biryani", "Meals", 140, false, true, "Spicy hyderabadi style chicken biryani");
        seedMenuItem("Paneer Butter Masala Meal", "Meals", 120, true, true, "Paneer curry with chapati and rice");
        seedMenuItem("Egg Fried Rice", "Meals", 100, false, false, "Egg and rice stir fried");
        seedMenuItem("Veg Puff", "Snacks", 25, true, false, "Flaky puff with vegetable stuffing");
        seedMenuItem("Samosa", "Snacks", 20, true, true, "Spicy potato samosa");
        seedMenuItem("Chicken Roll", "Snacks", 70, false, true, "Spicy chicken roll");
        seedMenuItem("French Fries", "Snacks", 55, true, false, "Salted crispy fries");
        seedMenuItem("Filter Coffee", "Beverages", 20, true, false, "Fresh South Indian filter coffee");
        seedMenuItem("Masala Chai", "Beverages", 15, true, false, "Ginger masala tea");
        seedMenuItem("Cold Coffee", "Beverages", 50, true, false, "Chilled coffee with ice cream");
        seedMenuItem("Fresh Lime Soda", "Beverages", 30, true, false, "Sweet or salted lime soda");
    }

    private void seedMenuItem(String name, String category, double price, boolean veg, boolean spicy,
                              String description) {
        if (!menu.existsByName(name)) {
            menu.save(new MenuItem(name, category, price, veg, spicy, description));
        }
    }
}
