package br.com.jonatasveloso.cifrasigreja.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        removerObrigatoriedadeDaCifra();
    }

    private void removerObrigatoriedadeDaCifra() {
        /*jdbcTemplate.execute("""
                ALTER TABLE musicas 
                ALTER COLUMN nome_arquivo_cifra DROP NOT NULL
                """);*/
    }
}