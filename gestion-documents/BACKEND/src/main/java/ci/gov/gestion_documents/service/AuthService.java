package ci.gov.gestion_documents.service;

import ci.gov.gestion_documents.dto.AuthRequest;
import ci.gov.gestion_documents.dto.AuthResponse;
import ci.gov.gestion_documents.dto.RegisterRequest;
import ci.gov.gestion_documents.model.Role;
import ci.gov.gestion_documents.domaine.Utilisateur;
import ci.gov.gestion_documents.repository.UtilisateurRepository;
import ci.gov.gestion_documents.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private  UtilisateurRepository utilisateurRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private  JwtService jwtService;

    public AuthResponse enregistrer(RegisterRequest request) {
        // vérifier si email déjà existant
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(Role.USER)
                .build();

        utilisateurRepository.save(utilisateur);

        String token = jwtService.generateToken(utilisateur.getEmail());
        return new AuthResponse(token, "Bearer");
    }

    public AuthResponse authenticate(AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        // si authentification réussie, générer token
        String token = jwtService.generateToken(request.getEmail());
        return new AuthResponse(token, "Bearer");
    }
}
