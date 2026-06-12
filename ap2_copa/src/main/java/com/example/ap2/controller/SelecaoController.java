package com.example.ap2.controller;

import com.example.ap2.model.Selecao;
import com.example.ap2.service.SelecaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/selecoes")
@Tag(name = "Seleções", description = "Endpoints para gerir as seleções da Copa do Mundo")
public class SelecaoController {

    private final SelecaoService selecaoService;

    public SelecaoController(SelecaoService selecaoService) {
        this.selecaoService = selecaoService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova seleção")
    public ResponseEntity<?> cadastrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\n  \"nomePais\": \"Brasil\",\n  \"tecnico\": \"Dorival Júnior\",\n  \"rankingFifa\": 5\n}"
                    ))
            ) @RequestBody Selecao selecao) {
        try {
            Selecao novaSelecao = selecaoService.cadastrar(selecao);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaSelecao);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Listar todas as seleções")
    public ResponseEntity<List<Selecao>> listarTodas() {
        return ResponseEntity.ok(selecaoService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar seleção por ID")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(selecaoService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma seleção (Atualização Parcial Inteligente)")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Selecao selecao) {
        try {
            selecaoService.atualizar(id, selecao);
            return ResponseEntity.ok("Seleção atualizada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma seleção")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            selecaoService.excluir(id);
            return ResponseEntity.ok("Seleção excluída com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}