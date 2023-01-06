package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String admin(Model model, Principal principal) {
        model.addAttribute("users", userService.allUsers());
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "admin/admin";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("user") User user, @RequestParam(value = "role", required = false) String role) {
        userService.save(user, role);
        return "redirect:/admin";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable(value = "id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @PutMapping("/edit")
    public String update(@ModelAttribute("user") User user, @RequestParam(value = "role", required = false) String role) {
        userService.update(user, role);
        return "redirect:/admin";
    }
}
