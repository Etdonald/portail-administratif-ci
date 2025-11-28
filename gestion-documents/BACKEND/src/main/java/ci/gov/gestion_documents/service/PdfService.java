package ci.gov.gestion_documents.service;

import ci.gov.gestion_documents.domaine.Demande;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;


import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public String genererCarteCniPdf(Demande demande) throws Exception {

        String dossier = "uploads/cni/";
        File dir = new File(dossier);
        if (!dir.exists()) dir.mkdirs();

        String pdfPath = dossier + "CNI_" + demande.getNumeroCNI() + ".pdf";

        // Format A6 pour une carte d'identité
        Rectangle pageSize = new Rectangle(420, 595);
        Document document = new Document(pageSize, 20, 20, 20, 20);

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

// 🔥 Passe pageSize
        writer.setPageEvent(new BackgroundImage("uploads/assets/amoirie.jpg", pageSize));

        document.open();



        // --- PHOTO ET INFORMATIONS PRINCIPALES ---
        addPhotoAndMainInfo(document, demande);

//        // --- INFORMATIONS PERSONNELLES ---
        addPersonalInfo(document, writer, demande);

//        // --- INFORMATIONS COMPLÉMENTAIRES ---
        addAdditionalInfo(document, writer, demande);

        // --- PIED DE PAGE AVEC SIGNATURE ---
        addFooter(document, demande);

        document.close();

        return pdfPath;
    }


    private void addPhotoAndMainInfo(Document document, Demande demande) throws DocumentException {
        PdfPTable photoTable = new PdfPTable(2);
        photoTable.setWidthPercentage(100);
        photoTable.setWidths(new float[]{1, 2});

        // Zone photo (à gauche) - PHOTO QUI PREND TOUT LE CADRE
        PdfPCell photoCell = new PdfPCell();
        photoCell.setFixedHeight(120); // Augmenté pour mieux remplir l'espace
        photoCell.setBorder(Rectangle.BOX);
        photoCell.setBorderColor(BaseColor.DARK_GRAY);
        photoCell.setBorderWidth(1.5f);
        photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        photoCell.setPadding(2f); // Réduit pour que la photo prenne plus d'espace

        boolean photoAdded = false;

        try {
            if (demande.getPhotoIdentitePath() != null && !demande.getPhotoIdentitePath().isEmpty()) {
                File photoFile = new File(demande.getPhotoIdentitePath());
                if (photoFile.exists()) {
                    Image photo = Image.getInstance(demande.getPhotoIdentitePath());

                    // Redimensionnement pour remplir tout l'espace disponible
                    // Taille maximale pour remplir la cellule (110x116 environ)
                    photo.scaleToFit(110, 116);

                    // Centrer l'image
                    photo.setAlignment(Image.ALIGN_CENTER);

                    // Ajouter directement l'image à la cellule
                    photoCell.setImage(photo);
                    photoAdded = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la photo: " + e.getMessage());
        }

        if (!photoAdded) {
            // Style pour le placeholder plus discret
            Font placeholderFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.LIGHT_GRAY);
            Paragraph placeholder = new Paragraph("PHOTO\nD'IDENTITÉ", placeholderFont);
            placeholder.setAlignment(Element.ALIGN_CENTER);

            // Fond très léger pour le placeholder
            photoCell.setBackgroundColor(new BaseColor(245, 245, 245));
            photoCell.addElement(placeholder);
        }

        photoTable.addCell(photoCell);

        // Informations principales (à droite) - PRÉNOM AVANT NOM
        PdfPCell infoCell = new PdfPCell();

        Font mainLabelFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
        Font mainValueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        // PRÉNOM(s) avec label
        Paragraph prenomLabel = new Paragraph("Prénom(s): ", mainLabelFont);
        Paragraph prenomValue = new Paragraph(getSafeString(demande.getPrenoms()), mainValueFont);

        Phrase prenomPhrase = new Phrase();
        prenomPhrase.add(prenomLabel);
        prenomPhrase.add(prenomValue);

        Paragraph prenoms = new Paragraph(prenomPhrase);
        prenoms.setSpacingAfter(3f);
        infoCell.addElement(prenoms);

// NOM avec label
        Paragraph nomLabel = new Paragraph("Nom: ", mainLabelFont);
        Paragraph nomValue = new Paragraph(getSafeString(demande.getNom()).toUpperCase(), mainValueFont);

        Phrase nomPhrase = new Phrase();
        nomPhrase.add(nomLabel);
        nomPhrase.add(nomValue);

        Paragraph nom = new Paragraph(nomPhrase);
        nom.setSpacingAfter(10f);
        infoCell.addElement(nom);

        // Numéro CNI seulement (NNI sera déplacé)
        Font numberFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.DARK_GRAY);

        Paragraph cniNumber = new Paragraph("CNI: " + getSafeString(demande.getNumeroCNI(), "---"), numberFont);
        cniNumber.setSpacingAfter(3f);
        infoCell.addElement(cniNumber);

        // Ajouter la date de naissance ici pour compléter
        Font infoFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
        Paragraph dateNaissance = new Paragraph("Né(e) le: " + formatDate(demande.getDateNaissance()), infoFont);
        infoCell.addElement(dateNaissance);

        infoCell.setBorder(Rectangle.NO_BORDER);
        photoTable.addCell(infoCell);

        document.add(photoTable);
        document.add(new Paragraph(" ")); // Espacement
    }

    private void addPhotoPlaceholder(PdfPCell photoCell, String message) {
        Font placeholderFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
        Paragraph placeholder = new Paragraph(message, placeholderFont);
        placeholder.setAlignment(Element.ALIGN_CENTER);
        photoCell.addElement(placeholder);

        // Ajouter un fond gris pour le placeholder
        photoCell.setBackgroundColor(new BaseColor(240, 240, 240));
    }

    private void addPersonalInfo(Document document, PdfWriter writer, Demande demande) throws DocumentException {
        // Titre section avec fond coloré
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);

        PdfPCell titleCell = new PdfPCell();
        titleCell.setBackgroundColor(new BaseColor(0, 102, 0)); // Vert
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPadding(5f);

        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        Paragraph sectionTitle = new Paragraph("INFORMATIONS PERSONNELLES", sectionFont);
        sectionTitle.setAlignment(Element.ALIGN_CENTER);
        titleCell.addElement(sectionTitle);

        titleTable.addCell(titleCell);
        document.add(titleTable);

        // Tableau des informations
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1, 2});

        Font labelFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

        addInfoRow(infoTable, "Sexe:", getSafeString(demande.getSexe()), labelFont, valueFont);
//        addInfoRow(infoTable, "Date de naissance:", formatDate(demande.getDateNaissance()), labelFont, valueFont);
        addInfoRow(infoTable, "Lieu de naissance:", getSafeString(demande.getLieuNaissance()), labelFont, valueFont);
        addInfoRow(infoTable, "Nationalité:", getSafeString(demande.getNationalite(), "Ivoirienne"), labelFont, valueFont);

        document.add(infoTable);
        document.add(new Paragraph(" ")); // Espacement
    }

    private void addAdditionalInfo(Document document, PdfWriter writer, Demande demande) throws DocumentException {
        // Titre section avec fond coloré
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);

        PdfPCell titleCell = new PdfPCell();
        titleCell.setBackgroundColor(new BaseColor(255, 153, 51)); // Orange
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPadding(5f);

        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        Paragraph sectionTitle = new Paragraph("INFORMATIONS COMPLÉMENTAIRES", sectionFont);
        sectionTitle.setAlignment(Element.ALIGN_CENTER);
        titleCell.addElement(sectionTitle);

        titleTable.addCell(titleCell);
        document.add(titleTable);

        // Tableau des informations complémentaires
        PdfPTable additionalTable = new PdfPTable(2);
        additionalTable.setWidthPercentage(100);
        additionalTable.setWidths(new float[]{1, 2});

        Font labelFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

        addInfoRow(additionalTable, "Profession:", getSafeString(demande.getProfession(), "Non renseignée"), labelFont, valueFont);
        addInfoRow(additionalTable, "Adresse:", getSafeString(demande.getAdresse(), "Non renseignée"), labelFont, valueFont);
        addInfoRow(additionalTable, "Date d'émission:", formatDate(demande.getDateEmission()), labelFont, valueFont);
        addInfoRow(additionalTable, "Date d'expiration:", formatDate(demande.getDateExpiration()), labelFont, valueFont);
        // AJOUT DU NNI ICI
        addInfoRow(additionalTable, "NNI:", getSafeString(demande.getNumeroNNI(), "---"), labelFont, valueFont);

        document.add(additionalTable);
        document.add(new Paragraph(" ")); // Espacement
    }

    private void addFooter(Document document, Demande demande) throws DocumentException {
        // Ligne de séparation
        Paragraph separation = new Paragraph(" ");
        separation.setSpacingBefore(10f);
        document.add(separation);

        // Zone signature
        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(100);
        footerTable.setWidths(new float[]{1, 1});

        // Date et cachet à gauche
        PdfPCell leftCell = new PdfPCell();
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);

        Paragraph dateParagraph = new Paragraph("Fait à Abidjan, le " + formatDate(LocalDate.now()), footerFont);
        leftCell.addElement(dateParagraph);

        Paragraph cachetParagraph = new Paragraph("◯ Cachet officiel ◯", footerFont);
        cachetParagraph.setSpacingBefore(5f);
        leftCell.addElement(cachetParagraph);

        leftCell.setBorder(Rectangle.NO_BORDER);
        footerTable.addCell(leftCell);

        // Signature à droite
        PdfPCell rightCell = new PdfPCell();

        Paragraph signatureLabel = new Paragraph("Signature de l'autorité", footerFont);
        signatureLabel.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(signatureLabel);

        Paragraph signatureLine = new Paragraph("___________________", footerFont);
        signatureLine.setAlignment(Element.ALIGN_CENTER);
        signatureLine.setSpacingBefore(5f);
        rightCell.addElement(signatureLine);

        Paragraph directeur = new Paragraph("Le Directeur Général\nONI", footerFont);
        directeur.setAlignment(Element.ALIGN_CENTER);
        directeur.setSpacingBefore(5f);
        rightCell.addElement(directeur);

        rightCell.setBorder(Rectangle.NO_BORDER);
        footerTable.addCell(rightCell);

        document.add(footerTable);

        // Pied de page final
        Font finalFooterFont = new Font(Font.FontFamily.HELVETICA, 6, Font.ITALIC, BaseColor.GRAY);
        Paragraph finalFooter = new Paragraph(
                "Document officiel - Reproduction interdite - " + getSafeString(demande.getNumeroCNI(), ""),
                finalFooterFont
        );
        finalFooter.setAlignment(Element.ALIGN_CENTER);
        finalFooter.setSpacingBefore(10f);
        document.add(finalFooter);
    }

    // Méthodes utilitaires

    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(new BaseColor(240, 240, 240));
        labelCell.setBorder(Rectangle.BOX);
        labelCell.setBorderColor(BaseColor.LIGHT_GRAY);
        labelCell.setPadding(4f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setBorderColor(BaseColor.LIGHT_GRAY);
        valueCell.setPadding(4f);
        table.addCell(valueCell);
    }

    private String getSafeString(String value) {
        return value != null ? value : "---";
    }

    private String getSafeString(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    private String formatDate(Object date) {
        if (date == null) {
            return "---";
        }

        try {
            if (date instanceof LocalDate) {
                return ((LocalDate) date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else if (date instanceof java.util.Date) {
                return new java.text.SimpleDateFormat("dd/MM/yyyy").format((java.util.Date) date);
            } else if (date instanceof java.sql.Date) {
                return new java.text.SimpleDateFormat("dd/MM/yyyy").format((java.sql.Date) date);
            } else {
                return date.toString();
            }
        } catch (Exception e) {
            return "---";
        }
    }

    private static class BackgroundImage extends PdfPageEventHelper {
        private final Image background;
        private final Rectangle pageSize;

        public BackgroundImage(String imagePath, Rectangle pageSize) {
            this.pageSize = pageSize;
            Image tempImage = null;
            try {
                tempImage = Image.getInstance(imagePath);
                tempImage.setAbsolutePosition(0, 0);
                tempImage.scaleAbsolute(pageSize.getWidth(), pageSize.getHeight());

            } catch (Exception e) {
                e.printStackTrace();
            }
            background = tempImage;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                PdfContentByte canvas = writer.getDirectContentUnder();

                // Ajouter l'image de fond
                if (background != null) {
                    canvas.addImage(background);
                }

                // Ajouter un overlay semi-transparent pour améliorer la lisibilité
                canvas.saveState();
                canvas.setColorFill(new BaseColor(255, 255, 255, 180)); // Blanc 70% transparent
                canvas.rectangle(0, 0, pageSize.getWidth(), pageSize.getHeight());
                canvas.fill();
                canvas.restoreState();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
