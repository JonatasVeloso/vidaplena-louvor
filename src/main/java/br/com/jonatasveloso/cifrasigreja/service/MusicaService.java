package br.com.jonatasveloso.cifrasigreja.service;

import br.com.jonatasveloso.cifrasigreja.model.Musica;
import br.com.jonatasveloso.cifrasigreja.repository.MusicaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class MusicaService {

    private final MusicaRepository musicaRepository;
    private final ArquivoService arquivoService;

    public MusicaService(MusicaRepository musicaRepository, ArquivoService arquivoService) {
        this.musicaRepository = musicaRepository;
        this.arquivoService = arquivoService;
    }

    public Musica cadastrar(Musica musica, MultipartFile arquivoCifra) {
        if (arquivoCifra != null && !arquivoCifra.isEmpty()) {
            String nomeArquivo = arquivoService.salvarCifra(arquivoCifra, musica.getNome());

            musica.setNomeArquivoCifra(nomeArquivo);
            musica.setCaminhoArquivoCifra("/cifras-arquivos/" + nomeArquivo);
        }

        normalizarCamposOpcionais(musica);

        return musicaRepository.save(musica);
    }

    public Musica editar(Long id, Musica dadosFormulario, MultipartFile arquivoCifra) {
        Musica musicaSalva = buscarPorId(id);

        musicaSalva.setNome(dadosFormulario.getNome());
        musicaSalva.setTom(dadosFormulario.getTom());
        musicaSalva.setLinkYoutube(dadosFormulario.getLinkYoutube());

        if (arquivoCifra != null && !arquivoCifra.isEmpty()) {
            String nomeArquivo = arquivoService.salvarCifra(arquivoCifra, dadosFormulario.getNome());

            musicaSalva.setNomeArquivoCifra(nomeArquivo);
            musicaSalva.setCaminhoArquivoCifra("/cifras-arquivos/" + nomeArquivo);
        }

        normalizarCamposOpcionais(musicaSalva);

        return musicaRepository.save(musicaSalva);
    }

    public List<Musica> listar(String busca) {
        if (busca == null || busca.isBlank()) {
            return musicaRepository.findAllByOrderByNomeAsc();
        }

        return musicaRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca);
    }

    public Musica buscarPorId(Long id) {
        return musicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Música não encontrada"));
    }

    private void normalizarCamposOpcionais(Musica musica) {
        if (musica.getTom() != null && musica.getTom().isBlank()) {
            musica.setTom(null);
        }

        if (musica.getLinkYoutube() != null && musica.getLinkYoutube().isBlank()) {
            musica.setLinkYoutube(null);
        }

        if (musica.getNomeArquivoCifra() != null && musica.getNomeArquivoCifra().isBlank()) {
            musica.setNomeArquivoCifra(null);
        }

        if (musica.getCaminhoArquivoCifra() != null && musica.getCaminhoArquivoCifra().isBlank()) {
            musica.setCaminhoArquivoCifra(null);
        }
    }
}