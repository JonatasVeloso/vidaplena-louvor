package br.com.jonatasveloso.cifrasigreja.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AppViewConfig {

    @Value("${app.base-path:}")
    private String basePath;

    @ModelAttribute("basePath")
    public String basePath() {
        return basePath;
    }
}