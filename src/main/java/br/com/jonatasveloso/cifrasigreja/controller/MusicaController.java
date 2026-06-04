package br.com.jonatasveloso.cifrasigreja.controller;

import br.com.jonatasveloso.cifrasigreja.model.Musica;
import br.com.jonatasveloso.cifrasigreja.service.MusicaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/musicas")
public class MusicaController {

    private final MusicaService musicaService;
    private final String basePath;

    public MusicaController(
            MusicaService musicaService,
            @Value("${app.base-path:}") String basePath
    ) {
        this.musicaService = musicaService;
        this.basePath = basePath;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca, Model model) {
        model.addAttribute("musicas", musicaService.listar(busca));
        model.addAttribute("busca", busca);
        return "musicas/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("musica", new Musica());
        model.addAttribute("modoEdicao", false);
        return "musicas/form";
    }

    @PostMapping
    public String cadastrar(
            @Valid @ModelAttribute("musica") Musica musica,
            BindingResult bindingResult,
            @RequestParam("arquivoCifra") MultipartFile arquivoCifra,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "musicas/form";
        }

        try {
            musicaService.cadastrar(musica, arquivoCifra);
            redirectAttributes.addFlashAttribute("sucesso", "Música cadastrada com sucesso!");
            return "redirect:" + basePath + "/musicas";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("modoEdicao", false);
            return "musicas/form";
        } catch (RuntimeException e) {
            model.addAttribute("erro", "Erro ao cadastrar música. Tente novamente.");
            model.addAttribute("modoEdicao", false);
            return "musicas/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Musica musica = musicaService.buscarPorId(id);

            model.addAttribute("musica", musica);
            model.addAttribute("modoEdicao", true);

            return "musicas/form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:" + basePath + "/musicas";
        }
    }

    @PostMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            @Valid @ModelAttribute("musica") Musica musica,
            BindingResult bindingResult,
            @RequestParam("arquivoCifra") MultipartFile arquivoCifra,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            return "musicas/form";
        }

        try {
            musicaService.editar(id, musica, arquivoCifra);
            redirectAttributes.addFlashAttribute("sucesso", "Música atualizada com sucesso!");
            return "redirect:" + basePath + "/musicas";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("modoEdicao", true);
            return "musicas/form";
        } catch (RuntimeException e) {
            model.addAttribute("erro", "Erro ao editar música. Tente novamente.");
            model.addAttribute("modoEdicao", true);
            return "musicas/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            musicaService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Música apagada com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao apagar música. Tente novamente.");
        }

        return "redirect:" + basePath + "/musicas";
    }
}