package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.models.Role;

import java.util.List;

public interface RoleService {

    Role getRoleById(Long id);

    void addRole(Role role);

    List<Role> getRoles();
}
