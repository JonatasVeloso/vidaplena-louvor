package br.com.jonatasveloso.cifrasigreja.service;

import br.com.jonatasveloso.cifrasigreja.model.Evento;
import br.com.jonatasveloso.cifrasigreja.model.Musica;
import br.com.jonatasveloso.cifrasigreja.model.RepertorioMusica;
import br.com.jonatasveloso.cifrasigreja.repository.EventoRepository;
import br.com.jonatasveloso.cifrasigreja.repository.MusicaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final MusicaRepository musicaRepository;

    public EventoService(EventoRepository eventoRepository, MusicaRepository musicaRepository) {
        this.eventoRepository = eventoRepository;
        this.musicaRepository = musicaRepository;
    }

    public List<Evento> listar() {
        return eventoRepository.findAllByOrderByDataDescHorarioDesc();
    }

    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
    }

    @Transactional
    public Evento cadastrar(Evento evento, List<Long> musicasIds) {
        montarRepertorio(evento, musicasIds);
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento editar(Long id, Evento dadosFormulario, List<Long> musicasIds) {
        Evento eventoSalvo = buscarPorId(id);

        eventoSalvo.setNome(dadosFormulario.getNome());
        eventoSalvo.setData(dadosFormulario.getData());
        eventoSalvo.setHorario(dadosFormulario.getHorario());

        eventoSalvo.getRepertorio().clear();
        montarRepertorio(eventoSalvo, musicasIds);

        return eventoRepository.save(eventoSalvo);
    }

    private void montarRepertorio(Evento evento, List<Long> musicasIds) {
        if (musicasIds == null) {
            musicasIds = new ArrayList<>();
        }

        int ordem = 1;

        for (Long musicaId : musicasIds) {
            if (musicaId == null) {
                continue;
            }

            Musica musica = musicaRepository.findById(musicaId)
                    .orElseThrow(() -> new IllegalArgumentException("Música não encontrada"));

            RepertorioMusica repertorioMusica = new RepertorioMusica();
            repertorioMusica.setEvento(evento);
            repertorioMusica.setMusica(musica);
            repertorioMusica.setOrdem(ordem);

            evento.getRepertorio().add(repertorioMusica);

            ordem++;
        }
    }
}