package ru.kata.spring.boot_security.demo.controller;

import com.mysql.cj.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.security.CustomUserDetailsService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, CustomUserDetailsService customUserDetailsService, RoleRepository roleRepository) {
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
        this.roleRepository = roleRepository;
    }

    @GetMapping(value = "")
    public String findAll(ModelMap model, Principal principal) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", new User());

        User userInis = customUserDetailsService.findByUsername(principal.getName());
        model.addAttribute("userInis", userInis);
        return "admin";
    }

//    @PostMapping("")
//    public String save(@ModelAttribute("user") User user) {
//        userService.save(user);
//        return "redirect:/admin";
//    }

    @PostMapping("")
    public String save(@ModelAttribute("user") User user, @RequestParam String roleName) {
        Role role = roleRepository.findByName(roleName);
        Role userRole = roleRepository.findByName("ROLE_USER");

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        if(roleName.equals("ROLE_ADMIN")) {
            roles.add(userRole);
        }
        user.setRoles(roles);
        userService.save(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}