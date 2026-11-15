package com.nmit.canteen.service;

import com.nmit.canteen.model.MenuItem;
import com.nmit.canteen.model.Order;
import com.nmit.canteen.model.OrderItem;
import com.nmit.canteen.repository.MenuItemRepository;
import com.nmit.canteen.repository.OrderRepository;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * AI features exposed through REST and consumed by React:
 *  1. Natural-language search ("spicy veg snacks under 60")
 *  2. Recommendations ("people who ordered this also ordered...") via co-occurrence mining
 * Both run offline. To use an LLM instead, replace parse() with an API call that returns the same filters.
 */
@Service
public class AiService {
    private static final Pattern PRICE = Pattern.compile("(?:under|below|less than|within|upto|up to|<)\\s*(?:rs\\.?|₹)?\\s*(\\d+)");
    private static final Set<String> STOP = Set.of("i", "want", "a", "an", "the", "some", "something", "me", "for", "with",
            "and", "to", "eat", "give", "show", "find", "need", "please", "food", "item", "items", "veg", "non", "spicy",
            "under", "below", "cheap", "than", "less");
    private static final Map<String, String> CATEGORY_WORDS = Map.of(
            "breakfast", "Breakfast", "meal", "Meals", "meals", "Meals", "lunch", "Meals",
            "snack", "Snacks", "snacks", "Snacks", "drink", "Beverages", "beverage", "Beverages",
            "beverages", "Beverages", "coffee", "Beverages");

    private final MenuItemRepository menu;
    private final OrderRepository orders;

    public AiService(MenuItemRepository menu, OrderRepository orders) {
        this.menu = menu; this.orders = orders;
    }

    public Map<String, Object> search(String query) {
        String q = query == null ? "" : query.toLowerCase().trim();
        Boolean veg = null;
        if (q.contains("non-veg") || q.contains("non veg") || q.contains("nonveg") || q.contains("chicken") || q.contains("egg")) veg = false;
        else if (q.contains("veg") || q.contains("vegetarian")) veg = true;
        boolean spicy = q.contains("spicy") || q.contains("hot");
        boolean cheap = q.contains("cheap");
        Double max = null;
        Matcher m = PRICE.matcher(q);
        if (m.find()) max = Double.parseDouble(m.group(1));
        String category = null;
        for (String w : q.split("\\W+")) if (CATEGORY_WORDS.containsKey(w)) category = CATEGORY_WORDS.get(w);
        List<String> keywords = Arrays.stream(q.split("\\W+"))
                .filter(w -> w.length() > 2 && !STOP.contains(w) && !CATEGORY_WORDS.containsKey(w) && !w.matches("\\d+"))
                .collect(Collectors.toList());

        final Boolean fVeg = veg; final Double fMax = max; final String fCat = category;
        List<MenuItem> results = menu.findAll().stream()
                .filter(MenuItem::isAvailable)
                .filter(i -> fVeg == null || i.isVeg() == fVeg)
                .filter(i -> !spicy || i.isSpicy())
                .filter(i -> fMax == null || i.getPrice() <= fMax)
                .filter(i -> fCat == null || i.getCategory().equals(fCat))
                .filter(i -> keywords.isEmpty() || keywords.stream().anyMatch(k ->
                        (i.getName() + " " + i.getDescription()).toLowerCase().contains(k)
                                || (k.endsWith("s") && (i.getName() + " " + i.getDescription()).toLowerCase().contains(k.substring(0, k.length() - 1)))))
                .sorted(cheap ? Comparator.comparingDouble(MenuItem::getPrice) : Comparator.comparing(MenuItem::getName))
                .collect(Collectors.toList());

        Map<String, Object> understood = new LinkedHashMap<>();
        understood.put("veg", veg);
        understood.put("spicy", spicy);
        understood.put("maxPrice", max);
        understood.put("category", category);
        understood.put("keywords", keywords);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("understood", understood);
        out.put("results", results);
        return out;
    }

    /** Items most frequently ordered together with the given item; falls back to overall popularity. */
    public List<MenuItem> recommend(Long itemId, int limit) {
        List<Order> all = orders.findAll();
        Map<Long, Integer> together = new HashMap<>();
        Map<Long, Integer> popularity = new HashMap<>();
        for (Order o : all) {
            Set<Long> ids = o.getItems().stream().map(i -> i.getMenuItem().getId()).collect(Collectors.toSet());
            for (OrderItem oi : o.getItems()) popularity.merge(oi.getMenuItem().getId(), oi.getQuantity(), Integer::sum);
            if (itemId != null && ids.contains(itemId))
                for (Long id : ids) if (!id.equals(itemId)) together.merge(id, 1, Integer::sum);
        }
        Map<Long, Integer> ranking = together.isEmpty() ? popularity : together;
        List<Long> topIds = ranking.entrySet().stream()
                .filter(e -> !e.getKey().equals(itemId))
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit).map(Map.Entry::getKey).collect(Collectors.toList());
        List<MenuItem> recs = new ArrayList<>(menu.findAllById(topIds));
        if (recs.size() < limit) { // cold start: pad with available items
            for (MenuItem m : menu.findAll()) {
                if (recs.size() >= limit) break;
                if (m.isAvailable() && !m.getId().equals(itemId) && recs.stream().noneMatch(r -> r.getId().equals(m.getId()))) recs.add(m);
            }
        }
        return recs;
    }
}
