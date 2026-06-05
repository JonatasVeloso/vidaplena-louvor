package br.com.jonatasveloso.cifrasigreja.service;

import br.com.jonatasveloso.cifrasigreja.model.Evento;
import br.com.jonatasveloso.cifrasigreja.model.Musica;
import br.com.jonatasveloso.cifrasigreja.model.RepertorioMusica;
import br.com.jonatasveloso.cifrasigreja.repository.EventoRepository;
import br.com.jonatasveloso.cifrasigreja.repository.MusicaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final MusicaRepository musicaRepository;

    public EventoService(EventoRepository eventoRepository, MusicaRepository musicaRepository) {
        this.eventoRepository = eventoRepository;
        this.musicaRepository = musicaRepository;
    }

    public List<Evento> listar() {
        return eventoRepository.findAllByOrderByDataAscHorarioAsc();
    }

    public Optional<Evento> buscarProximoEvento() {
        return eventoRepository.findFirstByDataGreaterThanEqualOrderByDataAscHorarioAsc(LocalDate.now());
    }

    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
    }

    @Transactional
    public Evento cadastrar(Evento evento, List<Long> musicasIds, Map<Long, Integer> ordensPorMusicaId) {
        montarRepertorio(evento, musicasIds, ordensPorMusicaId);
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento editar(Long id, Evento dadosFormulario, List<Long> musicasIds, Map<Long, Integer> ordensPorMusicaId) {
        Evento eventoSalvo = buscarPorId(id);

        eventoSalvo.setNome(dadosFormulario.getNome());
        eventoSalvo.setData(dadosFormulario.getData());
        eventoSalvo.setHorario(dadosFormulario.getHorario());

        eventoSalvo.getRepertorio().clear();
        montarRepertorio(eventoSalvo, musicasIds, ordensPorMusicaId);

        return eventoRepository.save(eventoSalvo);
    }

    @Transactional
    public void excluir(Long id) {
        Evento evento = buscarPorId(id);
        eventoRepository.delete(evento);
    }

    private void montarRepertorio(Evento evento, List<Long> musicasIds, Map<Long, Integer> ordensPorMusicaId) {
        if (musicasIds == null || musicasIds.isEmpty()) {
            return;
        }

        Map<Long, Integer> posicaoOriginal = criarMapaDePosicaoOriginal(musicasIds);

        List<Long> musicasOrdenadas = musicasIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted((id1, id2) -> compararPorOrdem(id1, id2, ordensPorMusicaId, posicaoOriginal))
                .collect(Collectors.toList());

        int ordemFinal = 1;

        for (Long musicaId : musicasOrdenadas) {
            Musica musica = musicaRepository.findById(musicaId)
                    .orElseThrow(() -> new IllegalArgumentException("Música não encontrada"));

            RepertorioMusica repertorioMusica = new RepertorioMusica();
            repertorioMusica.setEvento(evento);
            repertorioMusica.setMusica(musica);
            repertorioMusica.setOrdem(ordemFinal);

            evento.getRepertorio().add(repertorioMusica);

            ordemFinal++;
        }
    }

    private Map<Long, Integer> criarMapaDePosicaoOriginal(List<Long> musicasIds) {
        Map<Long, Integer> posicaoOriginal = new HashMap<>();

        for (int i = 0; i < musicasIds.size(); i++) {
            Long musicaId = musicasIds.get(i);

            if (musicaId != null && !posicaoOriginal.containsKey(musicaId)) {
                posicaoOriginal.put(musicaId, i);
            }
        }

        return posicaoOriginal;
    }

    private int compararPorOrdem(
            Long id1,
            Long id2,
            Map<Long, Integer> ordensPorMusicaId,
            Map<Long, Integer> posicaoOriginal
    ) {
        Integer ordem1 = ordensPorMusicaId != null ? ordensPorMusicaId.get(id1) : null;
        Integer ordem2 = ordensPorMusicaId != null ? ordensPorMusicaId.get(id2) : null;

        boolean temOrdem1 = ordem1 != null;
        boolean temOrdem2 = ordem2 != null;

        if (temOrdem1 && temOrdem2) {
            int comparacaoOrdem = ordem1.compareTo(ordem2);

            if (comparacaoOrdem != 0) {
                return comparacaoOrdem;
            }

            return Integer.compare(
                    posicaoOriginal.getOrDefault(id1, Integer.MAX_VALUE),
                    posicaoOriginal.getOrDefault(id2, Integer.MAX_VALUE)
            );
        }

        if (temOrdem1) {
            return -1;
        }

        if (temOrdem2) {
            return 1;
        }

        return Integer.compare(
                posicaoOriginal.getOrDefault(id1, Integer.MAX_VALUE),
                posicaoOriginal.getOrDefault(id2, Integer.MAX_VALUE)
        );
    }
}