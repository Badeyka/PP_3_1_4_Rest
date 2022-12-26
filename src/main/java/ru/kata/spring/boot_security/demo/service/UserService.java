package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UserService() {
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    @Transactional
    public boolean save(User user, String role) {

        if (userRepository.findByUsername(user.getUsername()) != null) {
            return false;
        }

        if (role.equals("ROLE_USER")) {

            user.setRoles(Collections.singleton(new Role(2L, "ROLE_USER")));
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return true;
        }
        if (role.equals("ROLE_ADMIN")) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            Set<Role> roles = new HashSet<>();
            roles.add(new Role(1L, "ROLE_ADMIN"));
            roles.add(new Role(2L, "ROLE_USER"));
            user.setRoles(roles);

            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteByUsername(String username) {
        User userFromDB = userRepository.findByUsername(username);
        if (userFromDB != null) {
            userRepository.delete(userFromDB);
            return true;
        }
        return false;
    }

    @Transactional
    public void update(User updateUser, String role) {
        User user = userRepository.findById(updateUser.getId()).get();
        user.setUsername(updateUser.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(updateUser.getPassword()));

        if (role.equals("ROLE_USER")) {
            Set<Role> roles = new HashSet<>();
            roles.add(new Role(2L, "ROLE_USER"));
            user.setRoles(roles);
        }
        if (role.equals("ROLE_ADMIN")) {
            Set<Role> roles = new HashSet<>();
            roles.add(new Role(1L, "ROLE_ADMIN"));
            roles.add(new Role(2L, "ROLE_USER"));
            user.setRoles(roles);
        }

        userRepository.save(user);
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {

        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }
}
