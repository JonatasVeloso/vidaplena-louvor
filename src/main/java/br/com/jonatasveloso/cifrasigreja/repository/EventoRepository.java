package br.com.jonatasveloso.cifrasigreja.repository;

import br.com.jonatasveloso.cifrasigreja.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findAllByOrderByDataAscHorarioAsc();

    Optional<Evento> findFirstByDataGreaterThanEqualOrderByDataAscHorarioAsc(LocalDate data);
}