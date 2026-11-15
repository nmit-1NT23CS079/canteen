package com.nmit.canteen.config;

import com.nmit.canteen.service.OrderRefGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Java-based (XML-free) configuration - Unit 3. */
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Prototype scope: a new instance every time it is requested (Unit 2 - bean scopes). */
    @Bean
    @Scope("prototype")
    public OrderRefGenerator orderRefGenerator() {
        return new OrderRefGenerator();
    }
}
