package br.com.jonatasveloso.cifrasigreja.repository;

import br.com.jonatasveloso.cifrasigreja.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findAllByOrderByDataDescHorarioDesc();
}