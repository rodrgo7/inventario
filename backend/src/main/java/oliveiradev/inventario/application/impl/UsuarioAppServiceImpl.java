package oliveiradev.inventario.application.impl;

import oliveiradev.inventario.application.dto.usuario.UsuarioCriacaoDTO;
import oliveiradev.inventario.application.dto.usuario.UsuarioRespostaDTO;
import oliveiradev.inventario.application.exception.EmailJaCadastradoException;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.service.UsuarioAppService;
import oliveiradev.inventario.domain.model.enums.NivelUsuario;
import oliveiradev.inventario.domain.model.usuarios.Usuario;
import oliveiradev.inventario.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioAppServiceImpl implements UsuarioAppService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioAppServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioRespostaDTO criarUsuario(UsuarioCriacaoDTO dto) throws EmailJaCadastradoException {
        Objects.requireNonNull(dto.nome(), "Nome do usuário não pode ser nulo.");
        Objects.requireNonNull(dto.email(), "Email do usuário não pode ser nulo.");
        Objects.requireNonNull(dto.senha(), "Senha do usuário não pode ser nula.");

        if (dto.nome().isBlank()) {
            throw new IllegalArgumentException("Nome do usuário não pode ser vazio.");
        }
        if (dto.email().isBlank() || !dto.email().contains("@")) {
            throw new IllegalArgumentException("Email do usuário inválido.");
        }
        if (dto.senha().length() < 6) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 6 caracteres.");
        }

        String emailNormalizado = dto.email().trim().toLowerCase();
        if (usuarioRepository.findByEmail(emailNormalizado).isPresent()) {
            throw new EmailJaCadastradoException(emailNormalizado);
        }

        String senhaHashed = passwordEncoder.encode(dto.senha());

        Usuario novoUsuario = new Usuario(
                dto.nome().trim(),
                emailNormalizado,
                senhaHashed
        );

        if (dto.niveis() != null && !dto.niveis().isEmpty()) {
            dto.niveis().forEach(novoUsuario::adicionarNivel);
        } else {
            novoUsuario.adicionarNivel(NivelUsuario.USUARIO_PADRAO);
        }

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        return converterParaRespostaDTO(usuarioSalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioRespostaDTO> buscarUsuarioPorId(String id) {
        Objects.requireNonNull(id, "ID do usuário não pode ser nulo.");
        return usuarioRepository.findById(id)
                .map(this::converterParaRespostaDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioRespostaDTO> buscarUsuarioPorEmail(String email) {
        Objects.requireNonNull(email, "Email do usuário não pode ser nulo.");
        String emailNormalizado = email.trim().toLowerCase();
        return usuarioRepository.findByEmail(emailNormalizado)
                .map(this::converterParaRespostaDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRespostaDTO> listarTodosUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::converterParaRespostaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioRespostaDTO atualizarNiveisUsuario(String usuarioId, Set<NivelUsuario> novosNiveis)
            throws RecursoNaoEncontradoException /*, RegraDeNegocioException */ {
        Objects.requireNonNull(usuarioId, "ID do usuário não pode ser nulo.");
        Objects.requireNonNull(novosNiveis, "Conjunto de novos níveis não pode ser nulo.");

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com ID " + usuarioId + " não encontrado."));

        usuario.definirNiveis(novosNiveis);

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return converterParaRespostaDTO(usuarioAtualizado);
    }

    // Método helper para converter Usuario para UsuarioRespostaDTO
    private UsuarioRespostaDTO converterParaRespostaDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioRespostaDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getNiveis(),
                usuario.getDataCriacao(),
                usuario.getDataModificacao()
        );
    }
}