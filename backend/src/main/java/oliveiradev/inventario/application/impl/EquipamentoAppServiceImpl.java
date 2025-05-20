package oliveiradev.inventario.application.impl;

import oliveiradev.inventario.application.dto.equipamento.EquipamentoAtualizacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoCriacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoRespostaDTO;
import oliveiradev.inventario.application.dto.equipamento.LogAlteracaoDTO; // Certifique-se que este DTO existe
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.exception.RegraDeNegocioException;
import oliveiradev.inventario.application.service.EquipamentoAppService;
import oliveiradev.inventario.domain.model.equipamentos.Equipamento;
// LogAlteracao da entidade não é usado diretamente aqui, mas sim o DTO LogAlteracaoDTO
import oliveiradev.inventario.domain.repository.EquipamentoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Para StringUtils.hasText

import java.util.List;
import java.util.Objects; // Para Objects.equals e Objects.requireNonNull
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

    private String getUsuarioAutenticadoEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername(); // Geralmente o email
            }
            return principal.toString(); // Fallback se o principal não for UserDetails
        }
        return "sistema"; // Usuário padrão para logs se não houver autenticação (ex: processos batch)
    }

    private EquipamentoRespostaDTO converterParaRespostaDTO(Equipamento equipamento) {
        if (equipamento == null) {
            return null;
        }
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
        Objects.requireNonNull(dto.nome(), "Nome não pode ser nulo no DTO de criação.");
        Objects.requireNonNull(dto.numeroDeSerie(), "Número de série não pode ser nulo no DTO de criação.");
        // A validação de @NotBlank e @Size nos DTOs já deve ter sido tratada pelo @Valid no controller

        logger.info("Tentativa de criar equipamento com número de série: {}", dto.numeroDeSerie());

        String numeroDeSerieNormalizado = dto.numeroDeSerie().trim();
        if (equipamentoRepository.findByNumeroDeSerie(numeroDeSerieNormalizado).isPresent()) {
            throw new RegraDeNegocioException("Número de série '" + numeroDeSerieNormalizado + "' já cadastrado.");
        }

        Equipamento equipamento = new Equipamento(
                dto.nome(), // A entidade Equipamento já faz trim()
                numeroDeSerieNormalizado,
                dto.descricaoDetalhada() // A entidade Equipamento já faz trim() se não nulo
        );

        String usuarioLogado = getUsuarioAutenticadoEmail();
        // Adiciona um log semântico de criação. Os campos @CreatedBy/@CreatedDate são automáticos.
        equipamento.adicionarLog(usuarioLogado, "Equipamento registrado no sistema por " + usuarioLogado + ".");

        Equipamento equipamentoSalvo = equipamentoRepository.save(equipamento);
        logger.info("Equipamento '{}' criado com ID: {} por {}", equipamentoSalvo.getNome(), equipamentoSalvo.getId(), usuarioLogado);

        return converterParaRespostaDTO(equipamentoSalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EquipamentoRespostaDTO> buscarEquipamentoPorId(String id) {
        Objects.requireNonNull(id, "ID do equipamento não pode ser nulo.");
        return equipamentoRepository.findById(id).map(this::converterParaRespostaDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EquipamentoRespostaDTO> buscarEquipamentoPorNumeroDeSerie(String numeroDeSerie) {
        Objects.requireNonNull(numeroDeSerie, "Número de série do equipamento não pode ser nulo.");
        return equipamentoRepository.findByNumeroDeSerie(numeroDeSerie.trim()).map(this::converterParaRespostaDTO);
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
        Objects.requireNonNull(id, "ID do equipamento não pode ser nulo.");
        Objects.requireNonNull(dto, "DTO de atualização não pode ser nulo.");
        logger.info("Tentativa de atualizar equipamento ID: {}", id);

        Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipamento com ID " + id + " não encontrado."));

        String usuarioLogado = getUsuarioAutenticadoEmail();
        boolean modificado = false;

        // Atualizar nome
        if (dto.nome() != null) { // Se o campo nome foi fornecido no DTO
            if (!StringUtils.hasText(dto.nome().trim())) { // Validação adicional se o DTO permitir strings vazias mas a regra de negócio não
                throw new IllegalArgumentException("O nome, se fornecido para atualização, não pode ser vazio.");
            }
            if (!Objects.equals(equipamento.getNome(), dto.nome().trim())) {
                equipamento.alterarNome(dto.nome(), usuarioLogado); // A entidade faz trim()
                modificado = true;
            }
        }

        // Atualizar descrição detalhada
        // Permite que a descrição seja definida como nula ou vazia se explicitamente passada no DTO
        if (dto.descricaoDetalhada() != null) {
            if (!Objects.equals(equipamento.getDescricaoDetalhada(), dto.descricaoDetalhada().trim())) {
                equipamento.alterarDescricaoDetalhada(dto.descricaoDetalhada(), usuarioLogado); // A entidade faz trim() se não nulo
                modificado = true;
            }
        }
        if (modificado) {
            // @LastModifiedBy e @LastModifiedDate serão atualizados automaticamente pela auditoria.
            Equipamento equipamentoAtualizado = equipamentoRepository.save(equipamento);
            logger.info("Equipamento ID: {} atualizado por {}", id, usuarioLogado);
            return converterParaRespostaDTO(equipamentoAtualizado);
        } else {
            logger.info("Nenhuma alteração aplicável detectada para o equipamento ID: {}. Nenhuma atualização realizada.", id);
            return converterParaRespostaDTO(equipamento); // Retorna o estado atual se nada mudou
        }
    }

    @Override
    @Transactional
    public void deletarEquipamento(String id) throws RecursoNaoEncontradoException {
        Objects.requireNonNull(id, "ID do equipamento para deleção não pode ser nulo.");
        String usuarioLogado = getUsuarioAutenticadoEmail();
        logger.info("Tentativa de deletar equipamento ID: {} por usuário {}", id, usuarioLogado);

        if (!equipamentoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Equipamento com ID " + id + " não encontrado para deleção.");
        }

        // Log ANTES da deleção (este log não ficará no documento do equipamento se for hard delete)
        logger.info("Equipamento ID: {} está sendo deletado por {}", id, usuarioLogado);

        equipamentoRepository.deleteById(id);
        logger.info("Equipamento ID: {} deletado com sucesso por {}", id, usuarioLogado);
    }
}