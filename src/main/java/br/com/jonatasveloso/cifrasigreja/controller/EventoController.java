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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        model.addAttribute("ordensSelecionadas", Map.of());
        model.addAttribute("modoEdicao", false);

        return "calendario/form";
    }

    @PostMapping
    public String cadastrar(
            @Valid @ModelAttribute("evento") Evento evento,
            BindingResult bindingResult,
            @RequestParam(value = "musicasIds", required = false) List<Long> musicasIds,
            @RequestParam Map<String, String> parametros,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Map<Long, Integer> ordensPorMusicaId = extrairOrdens(musicasIds, parametros);

        if (bindingResult.hasErrors()) {
            model.addAttribute("musicas", musicaService.listar(null));
            model.addAttribute("musicasSelecionadas", musicasIds != null ? musicasIds : List.of());
            model.addAttribute("ordensSelecionadas", ordensPorMusicaId);
            model.addAttribute("modoEdicao", false);
            return "calendario/form";
        }

        try {
            eventoService.cadastrar(evento, musicasIds, ordensPorMusicaId);
            redirectAttributes.addFlashAttribute("sucesso", "Evento cadastrado com sucesso!");
            return "redirect:" + basePath + "/calendario";
        } catch (RuntimeException e) {
            model.addAttribute("erro", "Erro ao cadastrar evento.");
            model.addAttribute("musicas", musicaService.listar(null));
            model.addAttribute("musicasSelecionadas", musicasIds != null ? musicasIds : List.of());
            model.addAttribute("ordensSelecionadas", ordensPorMusicaId);
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

            Map<Long, Integer> ordensSelecionadas = new HashMap<>();

            evento.getRepertorio().forEach(repertorioMusica ->
                    ordensSelecionadas.put(
                            repertorioMusica.getMusica().getId(),
                            repertorioMusica.getOrdem()
                    )
            );

            model.addAttribute("evento", evento);
            model.addAttribute("musicas", musicaService.listar(null));
            model.addAttribute("musicasSelecionadas", musicasSelecionadas);
            model.addAttribute("ordensSelecionadas", ordensSelecionadas);
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
            @RequestParam Map<String, String> parametros,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Map<Long, Integer> ordensPorMusicaId = extrairOrdens(musicasIds, parametros);

        if (bindingResult.hasErrors()) {
            model.addAttribute("musicas", musicaService.listar(null));
            model.addAttribute("musicasSelecionadas", musicasIds != null ? musicasIds : List.of());
            model.addAttribute("ordensSelecionadas", ordensPorMusicaId);
            model.addAttribute("modoEdicao", true);
            return "calendario/form";
        }

        try {
            eventoService.editar(id, evento, musicasIds, ordensPorMusicaId);
            redirectAttributes.addFlashAttribute("sucesso", "Evento atualizado com sucesso!");
            return "redirect:" + basePath + "/calendario/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("erro", "Erro ao editar evento.");
            model.addAttribute("musicas", musicaService.listar(null));
            model.addAttribute("musicasSelecionadas", musicasIds != null ? musicasIds : List.of());
            model.addAttribute("ordensSelecionadas", ordensPorMusicaId);
            model.addAttribute("modoEdicao", true);
            return "calendario/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            eventoService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Evento apagado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao apagar evento. Tente novamente.");
        }

        return "redirect:" + basePath + "/calendario";
    }

    private Map<Long, Integer> extrairOrdens(List<Long> musicasIds, Map<String, String> parametros) {
        Map<Long, Integer> ordens = new HashMap<>();

        if (musicasIds == null || musicasIds.isEmpty()) {
            return ordens;
        }

        for (Long musicaId : musicasIds) {
            String chave = "ordemMusica_" + musicaId;
            String valor = parametros.get(chave);

            if (valor == null || valor.isBlank()) {
                continue;
            }

            try {
                int ordem = Integer.parseInt(valor);

                if (ordem > 0) {
                    ordens.put(musicaId, ordem);
                }
            } catch (NumberFormatException ignored) {
                // Se a ordem for inválida, o sistema ignora e mantém no final.
            }
        }

        return ordens;
    }
}