package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;
import javax.annotation.PostConstruct;

@Service
public class TestUsersForDatabase {
    private final UserService userService;

    @Autowired
    public TestUsersForDatabase(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    @PostConstruct
    public void init() {

        User admin = new User("admin", "admin");
        User user = new User("user", "user");

        userService.save(admin, "ROLE_ADMIN");
        userService.save(user, "ROLE_USER");
    }
}
