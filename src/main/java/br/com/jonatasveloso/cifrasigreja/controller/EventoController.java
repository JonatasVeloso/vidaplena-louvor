package br.com.jonatasveloso.cifrasigreja.controller;

import br.com.jonatasveloso.cifrasigreja.model.Evento;
import br.com.jonatasveloso.cifrasigreja.service.EventoService;
import br.com.jonatasveloso.cifrasigreja.service.MusicaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/calendario")
public class EventoController {

    private final EventoService eventoService;
    private final MusicaService musicaService;
    private final String basePath;

    public EventoController(
            EventoService eventoService,
            MusicaService musicaService,
            @Value("${app.base-path:}") String basePath
    ) {
        this.eventoService = eventoService;
        this.musicaService = musicaService;
        this.basePath = basePath;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("eventos", eventoService.listar());
        return "calendario/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("evento", new Evento());
        model.addAttribute("musicas", musicaService.listar(null));
        model.addAttribute("musicasSelecionadas", List.of());
        model.addAttribute("modoEdicao", false);

        return "calendario/form";
    }

    @PostMapping
    public String cadastrar(
            @Valid @ModelAttribute("evento") Evento evento,
            BindingResult bindingResult,
            @RequestParam(value = "musicasIds", required = false) List<Long> musicasIds,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("musicas", musicaService.listar(null));
            model.addAttribute("musicasSelecionadas", musicasIds != null ? musicasIds : List.of());
            model.addAttribute("modoEdicao", false);
            return "calendario/form";
        }

        try {
            eventoService.cadastrar(evento, musicasIds);
            redirectAttributes.addFlashAttribute("sucesso", "Evento cadastrado com sucesso!");
            return "redirect:" + basePath + "/calendario";
        } catch (RuntimeException e) {
            model.addAttribute("erro", "Erro ao cadastrar evento.");
            model.addAttribute("musicas", musicaService.listar(null));
            model.addAttribute("musicasSelecionadas", musicasIds != null ? musicasIds : List.of());
            model.addAttribute("modoEdicao", false);
            return "calendario/form";
        }
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("evento", eventoService.buscarPorId(id));
            return "calendario/detalhe";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:" + basePath + "/calendario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Evento evento = eventoService.buscarPorId(id);

            List<Long> musicasSelecionadas = evento.getRepertorio()
                    .stream()
                    .map(repertorioMusica -> repertorioMusica.getMusica().getId())
                    .toList();

            model.addAttribute("evento", evento);
            model.addAttribute("musicas", musicaService.listar(null));
            model.addAttribute("musicasSelecionadas", musicasSelecionadas);
            model.addAttribute("modoEdicao", true);

            return "calendario/form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:" + basePath + "/calendario";
        }
    }

    @PostMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            @Valid @ModelAttribute("evento") Evento evento,
            BindingResult bindingResult,
            @RequestParam(value = "musicasIds", required = false) List<Long> musicasIds,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("musicas", musicaService.listar(null));
            model.addAttribute("musicasSelecionadas", musicasIds != null ? musicasIds : List.of());
            model.addAttribute("modoEdicao", true);
            return "calendario/form";
        }

        try {
            eventoService.editar(id, evento, musicasIds);
            redirectAttributes.addFlashAttribute("sucesso", "Evento atualizado com sucesso!");
            return "redirect:" + basePath + "/calendario/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("erro", "Erro ao editar evento.");
            model.addAttribute("musicas", musicaService.listar(null));
            model.addAttribute("musicasSelecionadas", musicasIds != null ? musicasIds : List.of());
            model.addAttribute("modoEdicao", true);
            return "calendario/form";
        }
    }
}