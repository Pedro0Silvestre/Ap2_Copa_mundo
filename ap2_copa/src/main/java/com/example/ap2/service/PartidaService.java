package com.example.ap2.service;

import com.example.ap2.model.Partida;
import com.example.ap2.model.Selecao;
import com.example.ap2.repository.PartidaRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final SelecaoService selecaoService;

    public PartidaService(PartidaRepository partidaRepository, SelecaoService selecaoService) {
        this.partidaRepository = partidaRepository;
        this.selecaoService = selecaoService;
    }

    public Partida cadastrar(Partida partida) {
        if (partida.getDataPartida() == null) {
            throw new IllegalArgumentException("A data da partida é um campo obrigatório.");
        }

        if (partida.getEstadio() == null || partida.getEstadio().trim().isEmpty()) {
            throw new IllegalArgumentException("O estádio da partida é um campo obrigatório.");
        }

        if (partida.getFaseCompeticao() == null || partida.getFaseCompeticao().trim().isEmpty()) {
            throw new IllegalArgumentException("A fase da competição é um campo obrigatório (ex: Grupos, Quartas, Final).");
        }

        if (partida.getSelecoes() == null || partida.getSelecoes().size() != 2) {
            throw new IllegalArgumentException("Uma partida válida deve possuir exatamente 2 seleções participantes.");
        }

        List<Selecao> selecoesValidadas = new ArrayList<>();
        for (Selecao s : partida.getSelecoes()) {
            if (s.getId() == null) {
                throw new IllegalArgumentException("É necessário informar o ID válido de cada seleção participante.");
            }
            Selecao selecaoBanco = selecaoService.buscarPorId(s.getId());
            selecoesValidadas.add(selecaoBanco);
        }

        if (partida.getPlacar() == null || partida.getPlacar().trim().isEmpty()) {
            partida.setPlacar("0x0");
        }

        partida.setSelecoes(selecoesValidadas);

        return partidaRepository.save(partida);
    }

    public List<Partida> listarTodas() {
        return partidaRepository.findAll();
    }

    public Partida buscarPorId(Long id) {
        return partidaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partida com o ID " + id + " não foi encontrada."));
    }

    public void atualizar(Long id, Partida partidaAtualizada) {
        Partida partidaSalvaNoBanco = this.buscarPorId(id);

        Partida partidaFinal = new Partida();
        partidaFinal.setId(id);

        if (partidaAtualizada.getEstadio() != null && !partidaAtualizada.getEstadio().trim().isEmpty()) {
            partidaFinal.setEstadio(partidaAtualizada.getEstadio());
        } else {
            partidaFinal.setEstadio(partidaSalvaNoBanco.getEstadio());
        }

        if (partidaAtualizada.getFaseCompeticao() != null && !partidaAtualizada.getFaseCompeticao().trim().isEmpty()) {
            partidaFinal.setFaseCompeticao(partidaAtualizada.getFaseCompeticao());
        } else {
            partidaFinal.setFaseCompeticao(partidaSalvaNoBanco.getFaseCompeticao());
        }

        if (partidaAtualizada.getPlacar() != null && !partidaAtualizada.getPlacar().trim().isEmpty()) {
            partidaFinal.setPlacar(partidaAtualizada.getPlacar());
        } else {
            partidaFinal.setPlacar(partidaSalvaNoBanco.getPlacar());
        }

        if (partidaAtualizada.getDataPartida() != null) {
            partidaFinal.setDataPartida(partidaAtualizada.getDataPartida());
        } else {
            partidaFinal.setDataPartida(partidaSalvaNoBanco.getDataPartida());
        }

        if (partidaAtualizada.getSelecoes() != null && partidaAtualizada.getSelecoes().size() == 2) {
            List<Selecao> novasSelecoes = new ArrayList<>();
            for (Selecao s : partidaAtualizada.getSelecoes()) {
                novasSelecoes.add(selecaoService.buscarPorId(s.getId()));
            }
            partidaFinal.setSelecoes(novasSelecoes);
        } else {
            partidaFinal.setSelecoes(partidaSalvaNoBanco.getSelecoes());
        }

        partidaRepository.save(partidaFinal);
    }

    public void excluir(Long id) {
        Partida partida = this.buscarPorId(id);
        partidaRepository.delete(partida);
    }

    public List<Partida> listarPartidasPorSelecao(Long selecaoId) {
        selecaoService.buscarPorId(selecaoId);
        return partidaRepository.findBySelecoesId(selecaoId);
    }
}