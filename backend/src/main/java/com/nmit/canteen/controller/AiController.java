package com.nmit.canteen.controller;

import com.nmit.canteen.model.MenuItem;
import com.nmit.canteen.service.AiService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    private final AiService ai;
    public AiController(AiService ai) { this.ai = ai; }

    /** GET /api/ai/search?q=spicy veg snacks under 60 */
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String q) { return ai.search(q); }

    /** GET /api/ai/recommend?itemId=5&limit=3 */
    @GetMapping("/recommend")
    public List<MenuItem> recommend(@RequestParam(required = false) Long itemId,
                                    @RequestParam(defaultValue = "3") int limit) {
        return ai.recommend(itemId, limit);
    }
}
