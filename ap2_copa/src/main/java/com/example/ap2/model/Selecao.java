package com.example.ap2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "selecao")
public class Selecao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_pais", nullable = false, unique = true)
    private String nomePais;

    @Column(nullable = false)
    private String tecnico;

    @Column(name = "ranking_fifa", nullable = false)
    private Integer rankingFifa;

    // Relacionamento 1:N com Jogador
    // JsonIgnoreProperties impede o loop infinito na hora de gerar o JSON no Swagger
    @OneToMany(mappedBy = "selecao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("selecao")
    private List<Jogador> jogadores;
}
