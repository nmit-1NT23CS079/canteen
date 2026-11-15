package com.nmit.canteen.service;

import java.util.UUID;

/** Prototype-scoped bean (see AppConfig). */
public class OrderRefGenerator {
    public String next() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
