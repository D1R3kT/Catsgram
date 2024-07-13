package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("email должен быть указан");
        } else {
            for (User u : users.values()) {
                if (user.getEmail().equals(u.getEmail())) {
                    throw new DuplicatedDataException("Этот email уже используется");
                }
            }

            user.setId(getNextId());
            user.setRegistrationDate(Instant.now());
            users.put(user.getId(), user);
            return user;
        }
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getId() == null) {
                throw new ConditionsNotMetException("id должен быть указан");
            }
            if (newUser.getEmail() != null) {
                for (User user : users.values()) {
                    if (newUser.getEmail().equals(user.getEmail())) {
                        throw new DuplicatedDataException("Данный email уже используется");
                    } else {
                        oldUser.setEmail(newUser.getEmail());
                    }
                }
            }

            if (newUser.getPassword() != null) {
                oldUser.setPassword(newUser.getPassword());
            }

            if (newUser.getUsername() != null) {
                oldUser.setUsername(newUser.getUsername());
            }
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");

    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}




