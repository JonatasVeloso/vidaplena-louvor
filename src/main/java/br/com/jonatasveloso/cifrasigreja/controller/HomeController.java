package br.com.jonatasveloso.cifrasigreja.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final String basePath;

    public HomeController(@Value("${app.base-path:}") String basePath) {
        this.basePath = basePath;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:" + basePath + "/musicas";
    }
}