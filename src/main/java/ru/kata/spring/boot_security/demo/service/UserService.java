package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    User findById(Long id);

    User findByEmail(String email);

    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    void save(User user);

    void deleteById(Long id);

    void update(User updateUser);

    List<User> allUsers();
}
