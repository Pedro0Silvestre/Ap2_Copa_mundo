package com.example.ap2.controller;

import com.example.ap2.model.Partida;
import com.example.ap2.service.PartidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/partidas")
@Tag(name = "Partidas", description = "Endpoints para gerir as partidas e confrontos N:N")
public class PartidaController {

    private final PartidaService partidaService;

    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    @PostMapping
    @Operation(summary = "Agendar/Criar uma nova partida entre duas seleções")
    public ResponseEntity<?> cadastrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\n  \"dataPartida\": \"2026-12-18T16:00:00\",\n  \"estadio\": \"Lusail Stadium\",\n  \"faseCompeticao\": \"Final\",\n  \"placar\": \"0x0\",\n  \"selecoes\": [\n    { \"id\": 1 },\n    { \"id\": 2 }\n  ]\n}"
                    ))
            ) @RequestBody Partida partida) {
        try {
            Partida novaPartida = partidaService.cadastrar(partida);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaPartida);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Listar todas as partidas realizadas ou agendadas")
    public ResponseEntity<List<Partida>> listarTodas() {
        return ResponseEntity.ok(partidaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar partida por ID")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(partidaService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados ou o Placar de uma partida")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Partida partida) {
        try {
            partidaService.atualizar(id, partida);
            return ResponseEntity.ok("Partida atualizada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma partida do cronograma")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            partidaService.excluir(id);
            return ResponseEntity.ok("Partida excluída com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/selecao/{selecaoId}")
    @Operation(summary = "Consultar todas as partidas que uma seleção específica participou")
    public ResponseEntity<?> listarPorSelecao(@PathVariable Long selecaoId) {
        try {
            return ResponseEntity.ok(partidaService.listarPartidasPorSelecao(selecaoId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}