package com.example.ap2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "partida")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_partida", nullable = false)
    private LocalDateTime dataPartida;

    @Column(nullable = false)
    private String estadio;

    @Column(name = "fase_competicao", nullable = false)
    private String faseCompeticao;

    @Column(nullable = false)
    private String placar = "0x0"; // Valor padrão inicial

    @ManyToMany
    @JoinTable(
            name = "selecao_partida", // Nome da tabela associativa no MySQL
            joinColumns = @JoinColumn(name = "partida_id"), // FK para esta tabela (Partida)
            inverseJoinColumns = @JoinColumn(name = "selecao_id") // FK para a outra tabela (Selecao)
    )
    private List<Selecao> selecoes;
}