package oliveiradev.inventario.application.service;

import oliveiradev.inventario.application.dto.UsuarioCriacaoDTO;
import oliveiradev.inventario.application.dto.UsuarioRespostaDTO;
import oliveiradev.inventario.application.exception.EmailJaCadastradoException;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.domain.model.NivelUsuario;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UsuarioAppService {
    UsuarioRespostaDTO criarUsuario(UsuarioCriacaoDTO usuarioCriacaoDTO) throws EmailJaCadastradoException;

    Optional<UsuarioRespostaDTO> buscarUsuarioPorId(String id);

    Optional<UsuarioRespostaDTO> buscarUsuarioPorEmail(String email);

    List<UsuarioRespostaDTO> listarTodosUsuarios();

    UsuarioRespostaDTO atualizarNiveisUsuario(String usuarioId, Set<NivelUsuario> novosNiveis) throws RecursoNaoEncontradoException /*, RegraDeNegocioException */;

}