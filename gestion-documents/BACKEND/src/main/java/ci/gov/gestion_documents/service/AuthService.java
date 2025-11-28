package ci.gov.gestion_documents.service;

import ci.gov.gestion_documents.domaine.VerificationToken;
import ci.gov.gestion_documents.dto.AuthRequest;
import ci.gov.gestion_documents.dto.AuthResponse;
import ci.gov.gestion_documents.dto.RegisterRequest;
import ci.gov.gestion_documents.model.Role;
import ci.gov.gestion_documents.domaine.Utilisateur;
import ci.gov.gestion_documents.repository.UtilisateurRepository;
import ci.gov.gestion_documents.repository.VerificationTokenRepository;
import ci.gov.gestion_documents.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    @Transactional
    public void enregistrer(RegisterRequest request) {
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .actif(false)
                .emailVerifie(false)
                .role(Role.USER)
                .build();

        utilisateurRepository.save(utilisateur);

        String verificationToken = UUID.randomUUID().toString();
        VerificationToken tokenEntity = new VerificationToken();
        tokenEntity.setToken(verificationToken);
        tokenEntity.setUtilisateur(utilisateur);
        tokenEntity.setExpiration(Instant.now().plusSeconds(3600)); // 1h
        verificationTokenRepository.save(tokenEntity);

        try {
            String lien = "http://localhost:5050/api/auth/activation?token=" + verificationToken;
            emailService.envoyerEmail(
                    utilisateur.getEmail(),
                    "Confirmation de votre inscription - PDOCACI",
                    "Bonjour " + utilisateur.getPrenom() + ",\n\n" +
                            "Merci de vous être inscrit sur PDOCACI.\n" +
                            "Veuillez cliquer sur le lien ci-dessous pour activer votre compte :\n" +
                            lien + "\n\nCe lien expirera dans 1 heure.\n\n" +
                            "L'équipe PDOCACI."
            );
        } catch (Exception e) {
            System.err.println("⚠️ Email non envoyé, mais utilisateur créé: " + e.getMessage());
        }

    }

    public AuthResponse authenticate(AuthRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));

        if (!utilisateur.isActif()) {
            throw new RuntimeException("Votre compte n'est pas activé. Veuillez vérifier votre email.");
        }

        // Authentifier
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponse(jwtToken, "Bearer");
    }

    @Transactional
    public void verifyToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (verificationToken.getExpiration().isBefore(Instant.now())) {
            throw new RuntimeException("Le lien de vérification a expiré");
        }

        Utilisateur user = verificationToken.getUtilisateur();
        user.setActif(true);
        user.setEmailVerifie(true);
        utilisateurRepository.save(user);

        verificationTokenRepository.delete(verificationToken);

    }
}