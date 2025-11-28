package ci.gov.gestion_documents.service;

import ci.gov.gestion_documents.domaine.Demande;
import ci.gov.gestion_documents.domaine.Utilisateur;
import ci.gov.gestion_documents.dto.DemandeDto;
import ci.gov.gestion_documents.mapper.DemandeMapper;
import ci.gov.gestion_documents.model.StatutDemande;
import ci.gov.gestion_documents.model.TypeDemande;
import ci.gov.gestion_documents.repository.DemandeRepository;
import ci.gov.gestion_documents.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DemandeService {

    private final DemandeRepository demandeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DemandeMapper demandeMapper;
    @Autowired
    private PdfService pdfService;

    /**
     * 🔵 CRÉATION DE DEMANDE - UNIFIÉ
     */
    public DemandeDto creerDemande(DemandeDto demandeDto, String emailUtilisateur) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(emailUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Demande demande;

        // Création selon le type de demande
        switch (demandeDto.getTypeDemande()) {
            case CNI:
                demande = creerDemandeCni(demandeDto, utilisateur);
                break;
            // Ajouter d'autres cas plus tard: EXTRAIT, PERMIS, etc.
            default:
                throw new RuntimeException("Type de demande non supporté: " + demandeDto.getTypeDemande());
        }

        demandeRepository.save(demande);
        return demandeMapper.toDto(demande);
    }

    /**
     * 🔵 CRÉATION SPÉCIFIQUE CNI
     */
    private Demande creerDemandeCni(DemandeDto dto, Utilisateur utilisateur) {
        Demande demande = Demande.createDemandeCni(utilisateur);

        // Remplir les champs spécifiques CNI
        demande.setTypeDemandeCni(dto.getTypeDemandeCni());
        demande.setNom(dto.getNom());
        demande.setPrenoms(dto.getPrenoms());
        demande.setSexe(dto.getSexe());
        demande.setDateNaissance(dto.getDateNaissance());
        demande.setLieuNaissance(dto.getLieuNaissance());
        demande.setProfession(dto.getProfession());
        demande.setNationalite(dto.getNationalite());
        demande.setTaille(dto.getTaille());
        demande.setAdresse(dto.getAdresse());
        demande.setVille(dto.getVille());
        demande.setRegion(dto.getRegion());
        demande.setTelephone(dto.getTelephone());
        demande.setEmail(dto.getEmail());

        // Gestion des dates
        if (dto.getDateEmission() != null) {
            demande.setDateEmission(LocalDate.parse(dto.getDateEmission()));
        }
        if (dto.getDateExpiration() != null) {
            demande.setDateExpiration(LocalDate.parse(dto.getDateExpiration()));
        }

        demande.setLieuEmission(dto.getLieuEmission());
        demande.setAutoriteEmettrice(dto.getAutoriteEmettrice());
        demande.setPhotoIdentitePath(dto.getPhotoIdentitePath());
        demande.setAncienneCniPath(dto.getAncienneCniPath());

        return demande;
    }

    /**
     * 🔵 LISTER TOUTES LES DEMANDES (Admin)
     */
    public List<DemandeDto> listerToutesDemandes() {
        return demandeRepository.findAll()
                .stream()
                .map(demandeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 🔵 LISTER DEMANDES PAR TYPE
     */
    public List<DemandeDto> listerDemandesParType(TypeDemande typeDemande) {
        return demandeRepository.findByTypeDemande(typeDemande)
                .stream()
                .map(demandeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 🔵 LISTER MES DEMANDES (Utilisateur connecté)
     */
    public List<DemandeDto> listerMesDemandes(String email) {
        return demandeRepository.findByUtilisateurEmail(email)
                .stream()
                .map(demandeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 🔵 LISTER MES DEMANDES PAR TYPE
     */
    public List<DemandeDto> listerMesDemandesParType(String email, TypeDemande typeDemande) {
        return demandeRepository.findByUtilisateurEmailAndTypeDemande(email, typeDemande)
                .stream()
                .map(demandeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 🔵 VALIDER UNE DEMANDE (Admin)
     */
    public DemandeDto validerDemande(UUID demandeId) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        // Validation selon le type
        switch (demande.getTypeDemande()) {
            case CNI:
                return validerDemandeCni(demande);
            // Ajouter d'autres cas plus tard
            default:
                throw new RuntimeException("Validation non implémentée pour: " + demande.getTypeDemande());
        }
    }

    /**
     * 🔵 VALIDATION SPÉCIFIQUE CNI
     */
    private DemandeDto validerDemandeCni(Demande demande) {

        // Génération CNI + NNI + Dates
        demande.setNumeroCNI(genererNumeroCNI());
        demande.setNumeroNNI(genererNumeroNNI());

        LocalDate emission = LocalDate.now();
        LocalDate expiration = emission.plusYears(10);

        demande.setDateEmission(emission);
        demande.setDateExpiration(expiration);

        demande.setStatut(StatutDemande.APPROUVEE);
        demande.setTraitee(true);

        // Génération du PDF
        String pdfPath;
        try {
            pdfPath = pdfService.genererCarteCniPdf(demande);
            demande.setCheminCniPdf(pdfPath);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF : " + e.getMessage());
        }

        demandeRepository.save(demande);

        return demandeMapper.toDto(demande);
    }


    /**
     * 🔵 REJETER UNE DEMANDE (Admin)
     */
    public DemandeDto rejeterDemande(UUID demandeId, String commentaire) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        demande.setStatut(StatutDemande.REJETEE);
        demande.setTraitee(true);
        demande.setCommentaireAdmin(commentaire);

        demandeRepository.save(demande);
        return demandeMapper.toDto(demande);
    }

    /**
     * 🔵 RÉCUPÉRER UNE DEMANDE PAR ID
     */
    public DemandeDto getDemandeById(UUID id) {
        return demandeRepository.findById(id)
                .map(demandeMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
    }

    // Méthodes de génération des numéros
    private String genererNumeroCNI() {
        String prefix = "C";

        String timestamp = String.valueOf(System.currentTimeMillis());
        String lastDigits = timestamp.substring(timestamp.length() - 8);

        Random random = new Random();
        int randomFour = 1000 + random.nextInt(9000);

        return prefix + lastDigits + randomFour;
    }


    private String genererNumeroNNI() {
        Random random = new Random();
        StringBuilder nni = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            nni.append(random.nextInt(10));
        }
        return nni.toString();
    }
}