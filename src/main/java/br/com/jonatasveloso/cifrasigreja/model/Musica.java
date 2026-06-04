package br.com.jonatasveloso.cifrasigreja.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "musicas")
public class Musica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da música é obrigatório")
    @Column(nullable = false)
    private String nome;

    private String cantor;

    private String tom;

    private String linkYoutube;

    @Column(nullable = true)
    private String nomeArquivoCifra;

    @Column(nullable = true)
    private String caminhoArquivoCifra;

    private LocalDateTime dataCadastro;

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCantor() {
        return cantor;
    }

    public String getTom() {
        return tom;
    }

    public String getLinkYoutube() {
        return linkYoutube;
    }

    public String getNomeArquivoCifra() {
        return nomeArquivoCifra;
    }

    public String getCaminhoArquivoCifra() {
        return caminhoArquivoCifra;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCantor(String cantor) {
        this.cantor = cantor;
    }

    public void setTom(String tom) {
        this.tom = tom;
    }

    public void setLinkYoutube(String linkYoutube) {
        this.linkYoutube = linkYoutube;
    }

    public void setNomeArquivoCifra(String nomeArquivoCifra) {
        this.nomeArquivoCifra = nomeArquivoCifra;
    }

    public void setCaminhoArquivoCifra(String caminhoArquivoCifra) {
        this.caminhoArquivoCifra = caminhoArquivoCifra;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}