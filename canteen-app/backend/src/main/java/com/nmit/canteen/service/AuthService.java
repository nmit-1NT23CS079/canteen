package com.nmit.canteen.service;

import com.nmit.canteen.model.User;
import com.nmit.canteen.repository.UserRepository;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Simple in-memory token auth with role checks (enough for a mini project). */
@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    public AuthService(UserRepository users, PasswordEncoder encoder) {
        this.users = users; this.encoder = encoder;
    }

    public User register(String name, String email, String password) {
        if (users.findByEmail(email).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        return users.save(new User(name, email, encoder.encode(password), "STUDENT"));
    }

    public String login(String email, String password) {
        User u = users.findByEmail(email)
                .filter(x -> encoder.matches(password, x.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        String token = UUID.randomUUID().toString();
        sessions.put(token, u);
        return token;
    }

    public User requireUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        User u = sessions.get(authHeader.substring(7));
        if (u == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired");
        return u;
    }

    public User requireAdmin(String authHeader) {
        User u = requireUser(authHeader);
        if (!"ADMIN".equals(u.getRole()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
        return u;
    }
}
