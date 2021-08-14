package com.project.controllers;

import com.project.model.User;
import com.project.servImpls.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public String viewUserPage(Model model) {
        model.addAttribute("users", userService.listAll());
        return "users";
    }

    @RequestMapping(path = "/user/activate", method = RequestMethod.POST)
    public String enableUser(@RequestParam("username") String username) {
        User user = userService.getUserByUsername(username);
        user.setEnabled(true);
        userService.save(user);
        return "redirect:/users";
    }

    @RequestMapping(path = "/user/deactivate", method = RequestMethod.POST)
    public String disableUser(@RequestParam("username") String username) {
        User user = userService.getUserByUsername(username);
        user.setEnabled(false);
        userService.save(user);
        return "redirect:/users";
    }

    @RequestMapping(path = "/user/activateAll", method = RequestMethod.POST)
    public String enableAllUsers(@RequestParam("username") String username) {
        List<User> users = userService.listAll();
        for (User user : users) {
            if (!user.getUsername().equals(username)) {
                user.setEnabled(true);
                userService.save(user);
            }
        }

        return "redirect:/users";
    }

    @RequestMapping(path = "/user/deactivateAll", method = RequestMethod.POST)
    public String disableAllUsers(@RequestParam("username") String username) {
        List<User> users = userService.listAll();
        for (User user : users) {
            if (!user.getUsername().equals(username)) {
                user.setEnabled(false);
                userService.save(user);
            }
        }
        return "redirect:/users";
    }
}
