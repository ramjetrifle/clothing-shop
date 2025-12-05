package com.pm.clothingshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ViewController - Serves Thymeleaf pages
 */
@Controller
public class ViewController {
    @GetMapping("/")
    public String home() {
        return "redirect:/register";
    }
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @GetMapping("/categories")
    public String categoriesPage() {
        return "categories";
    }
    @GetMapping("/products")
    public String productsPage() {
        return "products";
    }
    @GetMapping("/cart")
    public String cartPage() {
        return "cart";
    }
}