package com.project.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String viewLoginPage() {
        return "landing";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login() {
        return "landing";
    }

    @GetMapping("/login_error")
    public String viewDefaultLoginErrorPage(Model model) {
        String error = "Щось пішло не так, спробуйте знов";
        model.addAttribute("error", error);
        return "landing";
    }
    @GetMapping("/login_error_bad_credentials")
    public String viewBadCrLoginErrorPage(Model model) {
        String error = "Перевірте введені дані";
        model.addAttribute("error", error);
        return "landing";
    }
    @GetMapping("/login_error_disabled")
    public String viewDisLoginErrorPage(Model model) {
        String error = "Акаунт не активовано, зверніться до адміністратора";
        model.addAttribute("error", error);
        return "landing";
    }

}
