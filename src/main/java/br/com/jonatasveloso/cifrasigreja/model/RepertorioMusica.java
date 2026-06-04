package br.com.jonatasveloso.cifrasigreja.model;

import jakarta.persistence.*;

@Entity
@Table(name = "repertorio_musicas")
public class RepertorioMusica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer ordem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musica_id", nullable = false)
    private Musica musica;

    public Long getId() {
        return id;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public Evento getEvento() {
        return evento;
    }

    public Musica getMusica() {
        return musica;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public void setMusica(Musica musica) {
        this.musica = musica;
    }
}