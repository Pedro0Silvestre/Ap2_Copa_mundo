package com.example.ap2.service;

import com.example.ap2.model.Selecao;
import com.example.ap2.repository.SelecaoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SelecaoService {

    private final SelecaoRepository selecaoRepository;

    public SelecaoService(SelecaoRepository selecaoRepository) {
        this.selecaoRepository = selecaoRepository;
    }

    public Selecao cadastrar(Selecao selecao) {
        if (selecao.getNomePais() == null || selecao.getNomePais().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do país é um campo obrigatório.");
        }

        if (selecao.getTecnico() == null || selecao.getTecnico().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do técnico é um campo obrigatório.");
        }

        if (selecao.getRankingFifa() == null || selecao.getRankingFifa() <= 0) {
            throw new IllegalArgumentException("O ranking da FIFA deve ser um número maior que zero.");
        }

        return selecaoRepository.save(selecao);
    }

    public List<Selecao> listarTodas() {
        return selecaoRepository.findAll();
    }

    public Selecao buscarPorId(Long id) {
        return selecaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seleção com o ID " + id + " não foi encontrada."));
    }

    public void atualizar(Long id, Selecao selecaoAtualizada) {
        Selecao selecaoSalvaNoBanco = this.buscarPorId(id);

        Selecao selecaoFinal = new Selecao();
        selecaoFinal.setId(id);

        if (selecaoAtualizada.getNomePais() != null && !selecaoAtualizada.getNomePais().trim().isEmpty()) {
            selecaoFinal.setNomePais(selecaoAtualizada.getNomePais());
        } else {
            selecaoFinal.setNomePais(selecaoSalvaNoBanco.getNomePais());
        }

        if (selecaoAtualizada.getTecnico() != null && !selecaoAtualizada.getTecnico().trim().isEmpty()) {
            selecaoFinal.setTecnico(selecaoAtualizada.getTecnico());
        } else {
            selecaoFinal.setTecnico(selecaoSalvaNoBanco.getTecnico());
        }

        if (selecaoAtualizada.getRankingFifa() != null) {
            if (selecaoAtualizada.getRankingFifa() <= 0) {
                throw new IllegalArgumentException("O ranking atualizado da FIFA deve ser maior que zero.");
            }
            selecaoFinal.setRankingFifa(selecaoAtualizada.getRankingFifa());
        } else {
            selecaoFinal.setRankingFifa(selecaoSalvaNoBanco.getRankingFifa());
        }

        selecaoRepository.save(selecaoFinal);
    }

    public void excluir(Long id) {
        Selecao selecao = this.buscarPorId(id);
        selecaoRepository.delete(selecao);
    }
}