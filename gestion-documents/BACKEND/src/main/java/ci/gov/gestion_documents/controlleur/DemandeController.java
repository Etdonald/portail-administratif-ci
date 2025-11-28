package ci.gov.gestion_documents.controlleur;

import ci.gov.gestion_documents.domaine.Demande;
import ci.gov.gestion_documents.dto.DemandeDto;
import ci.gov.gestion_documents.model.TypeDemande;
import ci.gov.gestion_documents.service.DemandeService;
import ci.gov.gestion_documents.repository.DemandeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
public class DemandeController {

    private final DemandeService demandeService;
    @Autowired
    private DemandeRepository demandeRepository;

    /**
     * ============================
     *   🔵 ENDPOINTS UTILISATEUR
     * ============================
     */

    // Créer une demande (unifié)
    @PostMapping(value = "/avec-fichiers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DemandeDto> creerDemandeAvecFichiers(
            @RequestPart("demande") String demandeJson,
            @RequestPart(value = "photoIdentite", required = false) MultipartFile photoIdentite,
            @RequestPart(value = "ancienneCni", required = false) MultipartFile ancienneCni,
            Authentication authentication) throws IOException {

        String email = authentication.getName();

        // Convertir le JSON en DemandeDto
        ObjectMapper objectMapper = new ObjectMapper();
        DemandeDto demandeDto = objectMapper.readValue(demandeJson, DemandeDto.class);

        // Gérer les fichiers uploadés
        if (photoIdentite != null && !photoIdentite.isEmpty()) {
            String photoPath = sauvegarderFichier(photoIdentite, "photos-identite");
            demandeDto.setPhotoIdentitePath(photoPath);
        }

        if (ancienneCni != null && !ancienneCni.isEmpty()) {
            String ancienneCniPath = sauvegarderFichier(ancienneCni, "anciennes-cni");
            demandeDto.setAncienneCniPath(ancienneCniPath);
        }

        DemandeDto demandeCree = demandeService.creerDemande(demandeDto, email);
        return ResponseEntity.ok(demandeCree);
    }

    private String sauvegarderFichier(MultipartFile fichier, String dossier) throws IOException {
        // Créer le répertoire s'il n'existe pas
        Path repertoire = Paths.get("uploads/" + dossier);
        if (!Files.exists(repertoire)) {
            Files.createDirectories(repertoire);
        }

        // Générer un nom de fichier unique
        String nomFichier = UUID.randomUUID().toString() + "_" + fichier.getOriginalFilename();
        Path cheminFichier = repertoire.resolve(nomFichier);

        // Sauvegarder le fichier
        Files.copy(fichier.getInputStream(), cheminFichier, StandardCopyOption.REPLACE_EXISTING);

        return cheminFichier.toString();
    }

    // Lister mes demandes (tous types)
    @GetMapping("/mes-demandes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<DemandeDto>> mesDemandes(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(demandeService.listerMesDemandes(email));
    }

    // Lister mes demandes par type
    @GetMapping("/mes-demandes/{type}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<DemandeDto>> mesDemandesParType(
            @PathVariable TypeDemande type,
            Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.ok(demandeService.listerMesDemandesParType(email, type));
    }

    // Récupérer une de mes demandes
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DemandeDto> getMaDemande(@PathVariable UUID id) {
        return ResponseEntity.ok(demandeService.getDemandeById(id));
    }

    /**
     * ============================
     *   🔵 ENDPOINTS ADMIN
     * ============================
     */

    // Lister toutes les demandes
    @GetMapping("/les-demandes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DemandeDto>> listerToutesDemandes() {
        return ResponseEntity.ok(demandeService.listerToutesDemandes());
    }

    // Lister demandes par type
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DemandeDto>> listerDemandesParType(@PathVariable TypeDemande type) {
        return ResponseEntity.ok(demandeService.listerDemandesParType(type));
    }

    // Valider une demande
    @PutMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DemandeDto> validerDemande(@PathVariable UUID id) {
        return ResponseEntity.ok(demandeService.validerDemande(id));
    }

    @GetMapping("/cni/{id}/pdf")
    public ResponseEntity<Resource> telechargerPdf(@PathVariable UUID id) {

        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        File file = new File(demande.getCheminCniPdf());

        if (!file.exists()) {
            throw new RuntimeException("PDF non généré");
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }


    // Rejeter une demande
    @PutMapping("/{id}/rejeter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DemandeDto> rejeterDemande(
            @PathVariable UUID id,
            @RequestBody String commentaire) {

        return ResponseEntity.ok(demandeService.rejeterDemande(id, commentaire));
    }

    // Récupérer une demande (admin)
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DemandeDto> getDemande(@PathVariable UUID id) {
        return ResponseEntity.ok(demandeService.getDemandeById(id));
    }
}