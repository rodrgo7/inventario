package oliveiradev.inventario.application.service;

import oliveiradev.inventario.application.dto.equipamento.EquipamentoAtualizacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoCriacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoRespostaDTO;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.exception.RegraDeNegocioException;

import java.util.List;
import java.util.Optional;

public interface EquipamentoAppService {
    EquipamentoRespostaDTO criarEquipamento(EquipamentoCriacaoDTO equipamentoCriacaoDTO) throws RegraDeNegocioException;

    Optional<EquipamentoRespostaDTO> buscarEquipamentoPorId(String id);

    Optional<EquipamentoRespostaDTO> buscarEquipamentoPorNumeroDeSerie(String numeroDeSerie);

    List<EquipamentoRespostaDTO> listarTodosEquipamentos();

    EquipamentoRespostaDTO atualizarEquipamento(String id, EquipamentoAtualizacaoDTO equipamentoAtualizacaoDTO)
            throws RecursoNaoEncontradoException, RegraDeNegocioException;

    void deletarEquipamento(String id) throws RecursoNaoEncontradoException;
}