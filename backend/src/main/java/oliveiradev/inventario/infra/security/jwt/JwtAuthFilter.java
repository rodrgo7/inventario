package oliveiradev.inventario.infra.security.jwt;

import oliveiradev.inventario.infra.security.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtAuthFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            logger.debug("doFilterInternal - JWT extraído: {}", jwt);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);
                logger.info("doFilterInternal - Username (email) do JWT: {}", username);

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                if (userDetails != null) {
                    logger.info("doFilterInternal - UserDetails carregado para {}: Authorities: {}", username, userDetails.getAuthorities()); // LOG

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("doFilterInternal - Autenticação definida no SecurityContext para: {}", username);
                } else {
                    logger.warn("doFilterInternal - UserDetails é nulo para o username: {}", username);
                }
            } else {
                if (!StringUtils.hasText(jwt)) {
                    logger.debug("doFilterInternal - JWT não encontrado na requisição.");
                } else {
                    logger.warn("doFilterInternal - Validação do JWT falhou para o token: {}", jwt);
                }
            }
        } catch (Exception ex) {
            logger.error("doFilterInternal - Não foi possível definir a autenticação do usuário no contexto de segurança", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}