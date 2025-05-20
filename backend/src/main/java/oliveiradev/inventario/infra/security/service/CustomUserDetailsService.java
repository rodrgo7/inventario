package oliveiradev.inventario.infra.security.service;

import oliveiradev.inventario.domain.model.usuarios.Usuario;
import oliveiradev.inventario.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String emailNormalizado = email.trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado com o email: " + emailNormalizado)
                );

        // Opção B: Adicionando o prefixo "ROLE_" ao nome do enum
        Collection<? extends GrantedAuthority> authorities = usuario.getNiveis().stream()
                .map(nivel -> new SimpleGrantedAuthority("ROLE_" + nivel.name()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getSenha(),
                true,
                true,
                true,
                true,
                authorities
        );
    }
}