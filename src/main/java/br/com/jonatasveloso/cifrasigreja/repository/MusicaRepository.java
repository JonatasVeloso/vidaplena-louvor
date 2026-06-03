package br.com.jonatasveloso.cifrasigreja.repository;

import br.com.jonatasveloso.cifrasigreja.model.Musica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicaRepository extends JpaRepository<Musica, Long> {

    List<Musica> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    List<Musica> findAllByOrderByNomeAsc();
}