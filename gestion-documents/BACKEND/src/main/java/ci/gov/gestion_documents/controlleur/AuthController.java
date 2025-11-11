package ci.gov.gestion_documents.controller;

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
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/inscription")
    public ResponseEntity<AuthResponse> inscription(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.enregistrer(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}
