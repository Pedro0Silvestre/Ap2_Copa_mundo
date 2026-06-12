package com.example.ap2.controller;

import com.example.ap2.model.Jogador;
import com.example.ap2.service.JogadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jogadores")
@Tag(name = "Jogadores", description = "Endpoints para gerir os atletas da competição")
public class JogadorController {

    private final JogadorService jogadorService;

    public JogadorController(JogadorService jogadorService) {
        this.jogadorService = jogadorService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo jogador vinculado a uma seleção")
    public ResponseEntity<?> cadastrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\n  \"nome\": \"Vinicius Jr.\",\n  \"numeroCamisa\": 7,\n  \"posicao\": \"Atacante\",\n  \"idade\": 25,\n  \"selecao\": {\n    \"id\": 1\n  }\n}"
                    ))
            ) @RequestBody Jogador jogador) {
        try {
            Jogador novoJogador = jogadorService.cadastrar(jogador);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoJogador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos os jogadores")
    public ResponseEntity<List<Jogador>> listarTodos() {
        return ResponseEntity.ok(jogadorService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar jogador por ID")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jogadorService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados de um jogador")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Jogador jogador) {
        try {
            jogadorService.atualizar(id, jogador);
            return ResponseEntity.ok("Jogador atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um jogador")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            jogadorService.excluir(id);
            return ResponseEntity.ok("Jogador excluído com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/selecao/{selecaoId}")
    @Operation(summary = "Consultar todos os jogadores de uma seleção específica")
    public ResponseEntity<?> listarPorSelecao(@PathVariable Long selecaoId) {
        try {
            return ResponseEntity.ok(jogadorService.listarJogadoresPorSelecao(selecaoId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}