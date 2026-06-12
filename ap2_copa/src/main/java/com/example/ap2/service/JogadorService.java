package com.example.ap2.service;

import com.example.ap2.model.Jogador;
import com.example.ap2.model.Selecao;
import com.example.ap2.repository.JogadorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JogadorService {

    private final JogadorRepository jogadorRepository;
    private final SelecaoService selecaoService;

    public JogadorService(JogadorRepository jogadorRepository, SelecaoService selecaoService) {
        this.jogadorRepository = jogadorRepository;
        this.selecaoService = selecaoService;
    }

    public Jogador cadastrar(Jogador jogador) {
        if (jogador.getNome() == null || jogador.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do jogador é um campo obrigatório.");
        }

        if (jogador.getPosicao() == null || jogador.getPosicao().trim().isEmpty()) {
            throw new IllegalArgumentException("A posição do jogador é um campo obrigatório.");
        }

        if (jogador.getNumeroCamisa() == null || jogador.getNumeroCamisa() <= 0 || jogador.getNumeroCamisa() > 99) {
            throw new IllegalArgumentException("O número da camisa deve ser entre 1 e 99.");
        }

        if (jogador.getIdade() == null || jogador.getIdade() < 15) {
            throw new IllegalArgumentException("O jogador deve ter uma idade válida para competição (mínimo 15 anos).");
        }

        if (jogador.getSelecao() == null || jogador.getSelecao().getId() == null) {
            throw new IllegalArgumentException("O jogador deve obrigatoriamente estar vinculado a uma seleção existente (informe o selecao.id).");
        }

        Selecao selecaoBanco = selecaoService.buscarPorId(jogador.getSelecao().getId());

        jogador.setSelecao(selecaoBanco);

        return jogadorRepository.save(jogador);
    }

    public List<Jogador> listarTodos() {
        return jogadorRepository.findAll();
    }

    public Jogador buscarPorId(Long id) {
        return jogadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jogador com o ID " + id + " não foi encontrado."));
    }

    public void atualizar(Long id, Jogador jogadorAtualizado) {
        Jogador jogadorSalvoNoBanco = this.buscarPorId(id);

        Jogador jogadorFinal = new Jogador();
        jogadorFinal.setId(id);

        if (jogadorAtualizado.getNome() != null && !jogadorAtualizado.getNome().trim().isEmpty()) {
            jogadorFinal.setNome(jogadorAtualizado.getNome());
        } else {
            jogadorFinal.setNome(jogadorSalvoNoBanco.getNome());
        }

        if (jogadorAtualizado.getPosicao() != null && !jogadorAtualizado.getPosicao().trim().isEmpty()) {
            jogadorFinal.setPosicao(jogadorAtualizado.getPosicao());
        } else {
            jogadorFinal.setPosicao(jogadorSalvoNoBanco.getPosicao());
        }

        if (jogadorAtualizado.getNumeroCamisa() != null) {
            if (jogadorAtualizado.getNumeroCamisa() <= 0 || jogadorAtualizado.getNumeroCamisa() > 99) {
                throw new IllegalArgumentException("O número atualizado da camisa deve ser entre 1 e 99.");
            }
            jogadorFinal.setNumeroCamisa(jogadorAtualizado.getNumeroCamisa());
        } else {
            jogadorFinal.setNumeroCamisa(jogadorSalvoNoBanco.getNumeroCamisa());
        }

        if (jogadorAtualizado.getIdade() != null) {
            if (jogadorAtualizado.getIdade() < 15) {
                throw new IllegalArgumentException("A idade atualizada é inválida.");
            }
            jogadorFinal.setIdade(jogadorAtualizado.getIdade());
        } else {
            jogadorFinal.setIdade(jogadorSalvoNoBanco.getIdade());
        }

        if (jogadorAtualizado.getSelecao() != null && jogadorAtualizado.getSelecao().getId() != null) {
            Selecao novaSelecao = selecaoService.buscarPorId(jogadorAtualizado.getSelecao().getId());
            jogadorFinal.setSelecao(novaSelecao);
        } else {
            jogadorFinal.setSelecao(jogadorSalvoNoBanco.getSelecao());
        }

        jogadorRepository.save(jogadorFinal);
    }

    public void excluir(Long id) {
        Jogador jogador = this.buscarPorId(id);
        jogadorRepository.delete(jogador);
    }

    public List<Jogador> listarJogadoresPorSelecao(Long selecaoId) {
        selecaoService.buscarPorId(selecaoId);
        return jogadorRepository.findBySelecaoId(selecaoId);
    }
}