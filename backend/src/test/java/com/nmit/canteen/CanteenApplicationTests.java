package com.nmit.canteen;

import static org.junit.jupiter.api.Assertions.*;

import com.nmit.canteen.service.AiService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CanteenApplicationTests {
    @Autowired AiService ai;

    @Test
    void contextLoads() {}

    @Test
    void nlSearchUnderPriceVegSnacks() {
        Map<String, Object> r = ai.search("veg snacks under 30");
        List<?> results = (List<?>) r.get("results");
        assertFalse(results.isEmpty());
    }
}
