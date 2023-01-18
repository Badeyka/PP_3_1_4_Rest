package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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
public class UserServiceImp implements UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImp(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UserServiceImp() {
    }

    @Override
    public User findById(Long id) {
        User user = userRepository.findByID(id);
        if (user.getRoles().size() == 2) {
            user.setRole("ADMIN");
        } else user.setRole("USER");

        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", email));
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    @Transactional
    @Override
    public boolean save(User user, String role) {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            return true;
        }

        if (role == null || role.equals("USER")) {

            user.setRoles(Collections.singleton(new Role(2L, "ROLE_USER")));
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return false;
        }
        if (role.equals("ADMIN")) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            Set<Role> roles = new HashSet<>();
            roles.add(new Role(1L, "ROLE_ADMIN"));
            roles.add(new Role(2L, "ROLE_USER"));
            user.setRoles(roles);

            userRepository.save(user);
            return false;
        }
        return true;
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        User userFromDB = userRepository.findByID(id);

        if (userFromDB != null) {
            userRepository.delete(userFromDB);
        }
    }

    @Transactional
    @Override
    public void update(User updateUser, String role) {
        User user = userRepository.findByID(updateUser.getId());

        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setAge(updateUser.getAge());
        user.setEmail(updateUser.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(updateUser.getPassword()));

        if (!(role == null)) {
            if (role.equals("USER")) {
                Set<Role> roles = new HashSet<>();
                roles.add(new Role(2L, "ROLE_USER"));
                user.setRoles(roles);
            }

            if (role.equals("ADMIN")) {
                Set<Role> roles = new HashSet<>();
                roles.add(new Role(1L, "ROLE_ADMIN"));
                roles.add(new Role(2L, "ROLE_USER"));
                user.setRoles(roles);
            }
        }
        userRepository.save(user);
    }

    @Override
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {

        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }
}
