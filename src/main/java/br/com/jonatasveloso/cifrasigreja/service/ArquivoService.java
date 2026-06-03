package br.com.jonatasveloso.cifrasigreja.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ArquivoService {

    private final Path uploadPath;

    public ArquivoService(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir);
        criarPastaSeNaoExistir();
    }

    public String salvarCifra(MultipartFile arquivo, String nomeMusica) {
        validarArquivo(arquivo);

        try {
            String extensao = obterExtensao(arquivo.getOriginalFilename());
            String nomeArquivo = gerarNomeArquivo(nomeMusica, extensao);

            Path destino = uploadPath.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            return nomeArquivo;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar a cifra", e);
        }
    }

    private void criarPastaSeNaoExistir() {
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar pasta de upload", e);
        }
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Nenhum arquivo foi enviado");
        }

        String nomeOriginal = arquivo.getOriginalFilename();

        if (nomeOriginal == null || !nomeOriginal.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Por enquanto, envie apenas arquivos PDF");
        }
    }

    private String obterExtensao(String nomeArquivo) {
        int ultimoPonto = nomeArquivo.lastIndexOf(".");

        if (ultimoPonto == -1) {
            return "";
        }

        return nomeArquivo.substring(ultimoPonto);
    }

    private String gerarNomeArquivo(String nomeMusica, String extensao) {
        String nomeNormalizado = Normalizer.normalize(nomeMusica, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-zA-Z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "")
                .toLowerCase();

        String dataHora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        return nomeNormalizado + "-" + dataHora + extensao;
    }
}