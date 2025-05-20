package oliveiradev.inventario.interfaces.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus; // Importe HttpStatus se for usar nos logs
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teste")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/protegido")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getMensagemProtegida() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "usuário desconhecido";
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }
        logger.info("/protegido: Acesso permitido para o usuário {}", username);
        return ResponseEntity.ok("Olá, " + username + "! Esta é uma mensagem protegida. Seu token JWT é válido!");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('USUARIO_ADMIN') or hasRole('USUARIO_MASTER')")
    public ResponseEntity<String> getMensagemAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "desconhecido";
        String authoritiesString = "nenhuma";

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
            authoritiesString = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(", "));
        }
        logger.info("/admin: Acesso permitido para o usuário '{}' com roles: [{}]", username, authoritiesString);
        return ResponseEntity.ok("Olá, " + username + " (Roles: " + authoritiesString + ")! Esta é uma mensagem para Admins e Masters.");
    }

    @GetMapping("/master")
    @PreAuthorize("hasRole('USUARIO_MASTER')")
    public ResponseEntity<String> getMensagemMaster() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            logger.warn("/master: Authentication é NULL no SecurityContextHolder. Acesso negado por PreAuthorize antes disso?");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Sem autenticação no contexto (erro inesperado).");
        }

        String username = "principal_desconhecido";
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }

        String authoritiesString = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
        logger.info("/master: Usuário autenticado: '{}', Authorities no SecurityContext: [{}]", username, authoritiesString);

        boolean hasMasterRoleInContext = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USUARIO_MASTER"));
        logger.info("/master: Verificação manual da role 'ROLE_USUARIO_MASTER' no contexto: {}", hasMasterRoleInContext);

        if (!hasMasterRoleInContext && authentication.isAuthenticated()) {
            logger.warn("/master: Usuário {} ESTÁ AUTENTICADO mas o contexto NÃO MOSTRA a role ROLE_USUARIOMASTER. Authorities no contexto: {}", username, authoritiesString);
        }

        return ResponseEntity.ok("Olá, " + username + "! Esta é uma mensagem apenas para Masters.");
    }

    @GetMapping("/publico")
    public ResponseEntity<String> getMensagemPublica() {
        logger.info("/publico: Acesso ao endpoint público.");
        return ResponseEntity.ok("Esta é uma mensagem pública, acessível a todos!");
    }
}