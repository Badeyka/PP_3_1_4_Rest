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


@Controller
@RequestMapping("/admin")
public class AdminController {
    private UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("users", userService.allUsers());
        return "admin/admin";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("user") User user) {
        return "admin/new";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("user") User user, @RequestParam(value="role") String role, Model model) {
        if (userService.save(user, "ROLE_USER")){
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "admin/new";
        }
        return "redirect:/admin";
    }

    @DeleteMapping("/{username}/delete")
    public String delete(@PathVariable(value = "username") String username) {
        userService.deleteByUsername(username);
    return "redirect:/admin";
    }

    @GetMapping("/edit/{username}")
    public String edit(Model model, @PathVariable(value = "username") String username) {
        model.addAttribute("user", userService.findByUsername(username));
        return "admin/edit";
    }

    @PutMapping("/edit")
    public String update(@ModelAttribute("user") User user, @RequestParam(value="role") String role) {
        userService.update(user, role);
        return "redirect:/admin";
    }
}
