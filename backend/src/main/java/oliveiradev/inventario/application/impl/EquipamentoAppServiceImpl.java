package oliveiradev.inventario.application.impl;

import oliveiradev.inventario.application.dto.equipamento.*;
import oliveiradev.inventario.application.dto.usuario.*;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.exception.RegraDeNegocioException;
import oliveiradev.inventario.application.service.EquipamentoAppService;
import oliveiradev.inventario.domain.model.Equipamento;
import oliveiradev.inventario.domain.repository.EquipamentoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EquipamentoAppServiceImpl implements EquipamentoAppService {
    private static final Logger logger = LoggerFactory.getLogger(EquipamentoAppServiceImpl.class);

    private final EquipamentoRepository equipamentoRepository;

    @Autowired
    public EquipamentoAppServiceImpl(EquipamentoRepository equipamentoRepository) {
        this.equipamentoRepository = equipamentoRepository;
    }

    // Método helper para obter o email do usuário autenticado (ou um placeholder)
    private String getUsuarioAutenticadoEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            return principal.toString();
        }
        return "sistema";
    }

    // Método helper para converter Entidade para DTO de Resposta
    private EquipamentoRespostaDTO converterParaRespostaDTO(Equipamento equipamento) {
        if (equipamento == null) return null;
        List<LogAlteracaoDTO> logDTOs = equipamento.getLogs().stream()
                .map(log -> new LogAlteracaoDTO(log.getDataHora(), log.getUsuarioResponsavel(), log.getDescricao()))
                .collect(Collectors.toList());

        return new EquipamentoRespostaDTO(
                equipamento.getId(),
                equipamento.getNome(),
                equipamento.getNumeroDeSerie(),
                equipamento.getDescricaoDetalhada(),
                equipamento.getDataInclusaoNoSistema(),
                equipamento.getDataUltimaModificacao(),
                equipamento.getCriadoPorUsuario(),
                equipamento.getModificadoPorUsuario(),
                logDTOs
        );
    }

    @Override
    @Transactional
    public EquipamentoRespostaDTO criarEquipamento(EquipamentoCriacaoDTO dto) throws RegraDeNegocioException {
        logger.info("Tentativa de criar equipamento com número de série: {}", dto.numeroDeSerie());

        // Validação de unicidade do número de série
        if (equipamentoRepository.findByNumeroDeSerie(dto.numeroDeSerie().trim()).isPresent()) {
            throw new RegraDeNegocioException("Número de série '" + dto.numeroDeSerie() + "' já cadastrado.");
        }

        Equipamento equipamento = new Equipamento(
                dto.nome(),
                dto.numeroDeSerie(),
                dto.descricaoDetalhada()
        );

        equipamento.adicionarLog(getUsuarioAutenticadoEmail(), "Equipamento criado no sistema.");


        Equipamento equipamentoSalvo = equipamentoRepository.save(equipamento);
        logger.info("Equipamento criado com ID: {}", equipamentoSalvo.getId());

        return converterParaRespostaDTO(equipamentoSalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EquipamentoRespostaDTO> buscarEquipamentoPorId(String id) {
        return equipamentoRepository.findById(id).map(this::converterParaRespostaDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EquipamentoRespostaDTO> buscarEquipamentoPorNumeroDeSerie(String numeroDeSerie) {
        return equipamentoRepository.findByNumeroDeSerie(numeroDeSerie).map(this::converterParaRespostaDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipamentoRespostaDTO> listarTodosEquipamentos() {
        return equipamentoRepository.findAll().stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EquipamentoRespostaDTO atualizarEquipamento(String id, EquipamentoAtualizacaoDTO dto)
            throws RecursoNaoEncontradoException, RegraDeNegocioException {
        logger.info("Tentativa de atualizar equipamento ID: {}", id);

        Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipamento com ID " + id + " não encontrado."));

        String usuarioLogado = getUsuarioAutenticadoEmail();
        boolean modificado = false;

        if (StringUtils.hasText(dto.nome()) && !Objects.equals(equipamento.getNome(), dto.nome().trim())) {
            equipamento.alterarNome(dto.nome(), usuarioLogado);
            modificado = true;
        }

        if (StringUtils.hasText(dto.descricaoDetalhada()) && !Objects.equals(equipamento.getDescricaoDetalhada(), dto.descricaoDetalhada().trim())) {
            equipamento.alterarDescricaoDetalhada(dto.descricaoDetalhada(), usuarioLogado);
            modificado = true;
        }

        if (modificado) {
            // O @LastModifiedBy e @LastModifiedDate serão atualizados automaticamente.
            Equipamento equipamentoAtualizado = equipamentoRepository.save(equipamento);
            logger.info("Equipamento ID: {} atualizado por {}", id, usuarioLogado);

            return converterParaRespostaDTO(equipamentoAtualizado);
        } else {
            logger.info("Nenhuma alteração detectada para o equipamento ID: {}. Nenhuma atualização realizada.", id);
            return converterParaRespostaDTO(equipamento); // Retorna o estado atual se nada mudou
        }
    }

    @Override
    @Transactional
    public void deletarEquipamento(String id) throws RecursoNaoEncontradoException {
        logger.info("Tentativa de deletar equipamento ID: {}", id);
        if (!equipamentoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Equipamento com ID " + id + " não encontrado para deleção.");
        }

        equipamentoRepository.deleteById(id);
        logger.info("Equipamento ID: {} deletado com sucesso.", id);
    }
}