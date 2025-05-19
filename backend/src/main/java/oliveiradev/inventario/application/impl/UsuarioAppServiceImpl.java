package oliveiradev.inventario.application.impl;

import oliveiradev.inventario.application.dto.UsuarioCriacaoDTO;
import oliveiradev.inventario.application.dto.UsuarioRespostaDTO;
import oliveiradev.inventario.application.exception.EmailJaCadastradoException;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.service.UsuarioAppService;
import oliveiradev.inventario.domain.model.NivelUsuario;
import oliveiradev.inventario.domain.model.Usuario;
import oliveiradev.inventario.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    @Transactional // Garante que a operação seja atômica
    public UsuarioRespostaDTO criarUsuario(UsuarioCriacaoDTO dto) throws EmailJaCadastradoException {
        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new IllegalArgumentException("Nome do usuário não pode ser vazio.");
        }
        if (dto.email() == null || dto.email().isBlank() || !dto.email().contains("@")) {
            throw new IllegalArgumentException("Email do usuário inválido.");
        }
        if (dto.senha() == null || dto.senha().isBlank()) {
            throw new IllegalArgumentException("Senha do usuário não pode ser vazia.");
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

        return new UsuarioRespostaDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                new HashSet<>(usuarioSalvo.getNiveis()),
                usuarioSalvo.getDataCriacao(),
                usuarioSalvo.getDataModificacao()
        );
    }

    @Override
    public Optional<UsuarioRespostaDTO> buscarUsuarioPorId(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<UsuarioRespostaDTO> buscarUsuarioPorEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<UsuarioRespostaDTO> listarTodosUsuarios() {
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public UsuarioRespostaDTO atualizarNiveisUsuario(String usuarioId, Set<NivelUsuario> novosNiveis)
            throws RecursoNaoEncontradoException {
        throw new UnsupportedOperationException("Ainda não implementado");
    }

    private UsuarioRespostaDTO converterParaRespostaDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioRespostaDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                new HashSet<>(usuario.getNiveis()),
                usuario.getDataCriacao(),
                usuario.getDataModificacao()
        );
    }
}