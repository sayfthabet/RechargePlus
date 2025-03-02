package tn.esprit.rechargeplus.services.InvestmentServices;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Project;
import tn.esprit.rechargeplus.repositories.InvestmentRepository.ProjectRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class PdfService {

    private final ProjectRepository projectRepository;

    public PdfService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public byte[] generateProjectPdf() throws IOException {
        List<Project> projects = projectRepository.findAll();
        DecimalFormat moneyFormat = new DecimalFormat("#,###.00");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true, true);

            // 🔹 Charger une police compatible Unicode
            PDType0Font font = PDType0Font.load(document, new File("C:/Windows/Fonts/arial.ttf"));
            contentStream.setFont(font, 10);

            // 🔹 Charger et ajouter le logo
            String logoPath = "C:/Users/achre/Desktop/Infini 1/semestre2/PIDEV/Logo/Recharge.jpg";
            PDImageXObject logo = PDImageXObject.createFromFile(logoPath, document);
            contentStream.drawImage(logo, 50, 750, 100, 50); // Position et taille du logo

            // 🔹 Ajouter un titre centré
            contentStream.setFont(font, 16);
            float titleWidth = font.getStringWidth("Liste complète des Projets") / 1000 * 16;
            float centerX = (page.getMediaBox().getWidth() - titleWidth) / 2;
            contentStream.beginText();
            contentStream.newLineAtOffset(centerX, 750);
            contentStream.showText("Liste complète des Projets");
            contentStream.endText();

            // 🔹 Ligne de séparation sous le titre
            contentStream.moveTo(50, 740);
            contentStream.lineTo(page.getMediaBox().getWidth() - 50, 740);
            contentStream.stroke();

            // 🔹 Configuration du tableau
            float margin = 50;
            float yStart = 700;
            float yPosition = yStart;
            float rowHeight = 20;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float[] columnWidths = {30, 80, 120, 80, 70, 90, 90, 90, 90, 80};

            // 🔹 En-têtes des colonnes
            String[] headers = {
                    "ID", "Titre", "Description", "Secteur", "Lieu", "Montant Requis",
                    "Montant Collecté", "Catégorie de Risque", "Investissement", "Statut"
            };

            // 🔹 Dessiner l'en-tête du tableau
            contentStream.setFont(font, 10);
            yPosition -= rowHeight;
            drawTableRow(contentStream, font, margin, yPosition, columnWidths, headers);
            drawTableLines(contentStream, margin, yPosition, tableWidth, columnWidths);
            contentStream.setFont(font, 8);

            // 🔹 Remplir le tableau avec les projets
            for (Project project : projects) {
                yPosition -= rowHeight;

                // Vérifier si une nouvelle page est nécessaire
                if (yPosition < 50) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true, true);
                    contentStream.setFont(font, 8);
                    yPosition = 700;

                    // 🔹 Redessiner l'en-tête sur la nouvelle page
                    drawTableRow(contentStream, font, margin, yPosition, columnWidths, headers);
                    drawTableLines(contentStream, margin, yPosition, tableWidth, columnWidths);
                    yPosition -= rowHeight;
                }

                String[] row = {
                        String.valueOf(project.getIdProject()),
                        sanitizeText(project.getTitle()),
                        sanitizeText(project.getDescription()),
                        sanitizeText(project.getSector()),
                        sanitizeText(project.getLocation()),
                        moneyFormat.format(project.getAmountRequested()),
                        moneyFormat.format(project.getAmountCollected()),
                        sanitizeText(project.getRiskCategory()),
                        moneyFormat.format(project.getInvestmentAmount()),
                        sanitizeText(project.getStatus() != null ? project.getStatus().name() : "PENDING")
                };

                drawTableRow(contentStream, font, margin, yPosition, columnWidths, row);
                drawTableLines(contentStream, margin, yPosition, tableWidth, columnWidths);
            }

            contentStream.close();

            // 🔹 Sauvegarde du PDF dans un flux
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            document.close();
            return outputStream.toByteArray();
        }
    }

    // 🔹 Fonction pour dessiner une ligne de tableau
    private void drawTableRow(PDPageContentStream contentStream, PDType0Font font, float x, float y, float[] widths, String[] texts) throws IOException {
        float currentX = x;
        for (int i = 0; i < texts.length; i++) {
            contentStream.beginText();
            contentStream.setFont(font, 8);
            contentStream.newLineAtOffset(currentX + 5, y + 5);
            contentStream.showText(texts[i] != null ? texts[i] : ""); // Éviter les nulls
            contentStream.endText();
            currentX += widths[i];
        }
    }

    // 🔹 Fonction pour dessiner les bordures du tableau
    private void drawTableLines(PDPageContentStream contentStream, float x, float y, float width, float[] widths) throws IOException {
        float currentX = x;
        float rowBottom = y - 20;

        // Dessiner la ligne horizontale
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + width, y);
        contentStream.stroke();

        // Dessiner les lignes verticales
        for (float columnWidth : widths) {
            contentStream.moveTo(currentX, y);
            contentStream.lineTo(currentX, rowBottom);
            contentStream.stroke();
            currentX += columnWidth;
        }

        // Dessiner la ligne du bas
        contentStream.moveTo(x, rowBottom);
        contentStream.lineTo(x + width, rowBottom);
        contentStream.stroke();
    }

    // 🔹 Fonction pour nettoyer le texte et éviter les erreurs de caractères
    private String sanitizeText(String text) {
        if (text == null) return "Non défini"; // Gérer les valeurs nulles
        return text.replace("\u202F", " ")  // Remplace U+202F par un espace normal
                .replace("\u00A0", " ")  // Remplace U+00A0 (NO-BREAK SPACE) par un espace normal
                .trim();  // Supprime les espaces inutiles
    }
}
