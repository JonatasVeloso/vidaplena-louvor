package br.com.jonatasveloso.cifrasigreja.controller;

import br.com.jonatasveloso.cifrasigreja.service.EventoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final EventoService eventoService;

    public HomeController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("proximoEvento", eventoService.buscarProximoEvento().orElse(null));
        return "home";
    }
}