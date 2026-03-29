package com.br.hsiphonesapi.security;

import com.br.hsiphonesapi.config.tenant.TenantContext;
import com.br.hsiphonesapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Se há tenant no contexto (request autenticado via JWT), usa filtro do Hibernate
        if (TenantContext.getTenantId() != null && TenantContext.getTenantId() > 0) {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        }

        // Sem tenant no contexto (durante login), busca sem filtro de tenant
        return userRepository.findByUsernameIgnoringTenant(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}
