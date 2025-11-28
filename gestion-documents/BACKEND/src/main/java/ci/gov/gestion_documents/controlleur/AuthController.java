package ci.gov.gestion_documents.controlleur;

import ci.gov.gestion_documents.dto.AuthRequest;
import ci.gov.gestion_documents.dto.AuthResponse;
import ci.gov.gestion_documents.dto.RegisterRequest;
import ci.gov.gestion_documents.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/inscription")
    public ResponseEntity<?> inscription(@RequestBody RegisterRequest request) {
        authService.enregistrer(request);
        return ResponseEntity.ok("Inscription réussie. Vérifiez votre email pour activer votre compte.");
    }

    @GetMapping("/activation")
    public ResponseEntity<?> verifyCompte(@RequestParam("token") String token) {
        authService.verifyToken(token);
        return ResponseEntity.ok("Compte activé avec succès. Vous pouvez maintenant vous connecter !");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}
