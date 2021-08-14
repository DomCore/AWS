package com.project.controllers;

import com.project.model.Product;
import com.project.servImpls.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class MainController implements ErrorController {
    private static final String PATH = "/error";


    @GetMapping("/")
    public String landing() {
        return "landing";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/get")
    public String get(Model model) {
/*        List<Product> products = productService.getProductsFromBox(1);
        model.addAttribute("products",products);*/
        return "index";
    }

    @GetMapping("/catalog")
    public String catalog() {
        return "catalog";
    }

    @RequestMapping(value = PATH)
    public String error() {
        return "errors/500";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
