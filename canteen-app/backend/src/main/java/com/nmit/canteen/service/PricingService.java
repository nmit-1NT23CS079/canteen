package com.nmit.canteen.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Singleton bean (default scope) with lifecycle callbacks and property injection. */
@Service
public class PricingService {
    private static final Logger log = LoggerFactory.getLogger(PricingService.class);

    @Value("${canteen.gst-rate}") private double gstRate;
    @Value("${canteen.discount-threshold}") private double discountThreshold;
    @Value("${canteen.discount-rate}") private double discountRate;

    @PostConstruct
    void init() { log.info("PricingService ready: GST={}, discount {} above {}", gstRate, discountRate, discountThreshold); }

    @PreDestroy
    void shutdown() { log.info("PricingService shutting down"); }

    public double discount(double subtotal) {
        return subtotal >= discountThreshold ? round(subtotal * discountRate) : 0;
    }

    public double tax(double subtotalAfterDiscount) { return round(subtotalAfterDiscount * gstRate); }

    private double round(double v) { return Math.round(v * 100.0) / 100.0; }
}
