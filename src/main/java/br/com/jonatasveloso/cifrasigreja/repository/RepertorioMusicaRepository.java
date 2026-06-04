package br.com.jonatasveloso.cifrasigreja.repository;

import br.com.jonatasveloso.cifrasigreja.model.RepertorioMusica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepertorioMusicaRepository extends JpaRepository<RepertorioMusica, Long> {

    void deleteByMusicaId(Long musicaId);
}