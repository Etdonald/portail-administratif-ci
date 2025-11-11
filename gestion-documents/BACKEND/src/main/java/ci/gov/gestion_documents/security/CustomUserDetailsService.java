package ci.gov.gestion_documents.security;

import ci.gov.gestion_documents.domaine.Utilisateur;
import ci.gov.gestion_documents.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec email: " + email));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name()));

        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasse())
                .authorities(authorities)
                .accountLocked(false)
                .disabled(!utilisateur.isActif())
                .build();
    }
}
