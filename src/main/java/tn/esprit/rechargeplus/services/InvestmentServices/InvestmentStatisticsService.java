package tn.esprit.rechargeplus.services.InvestmentServices;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Project;
import tn.esprit.rechargeplus.repositories.InvestmentRepository.InvestmentRequestRepository;
import tn.esprit.rechargeplus.repositories.InvestmentRepository.ProjectRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class InvestmentStatisticsService {

    private final InvestmentRequestRepository investmentRepo;
    private final ProjectRepository projectRepo;

    public InvestmentStatisticsService(InvestmentRequestRepository investmentRepo, ProjectRepository projectRepo) {
        this.investmentRepo = investmentRepo;
        this.projectRepo = projectRepo;
    }

    public byte[] generateInvestmentStatisticsExcel() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Investment Statistics");
        int rowNum = 0;

        // ✅ Nombre total d'investissements réalisés
        Row row1 = sheet.createRow(rowNum++);
        row1.createCell(0).setCellValue("Nombre total d'investissements");
        row1.createCell(1).setCellValue(investmentRepo.count());

        // ✅ Montant total investi
        Row row2 = sheet.createRow(rowNum++);
        row2.createCell(0).setCellValue("Montant total investi (€)");
        Double totalInvestment = investmentRepo.getTotalInvestedAmount();
        row2.createCell(1).setCellValue(totalInvestment != null ? totalInvestment : 0.0);

        // ✅ Répartition des investissements par secteur
        rowNum++;
        Row sectorHeader = sheet.createRow(rowNum++);
        sectorHeader.createCell(0).setCellValue("Investissements par secteur");
        sectorHeader.createCell(1).setCellValue("Montant (€)");

        List<Object[]> sectorInvestments = investmentRepo.getInvestmentBySector();
        for (Object[] obj : sectorInvestments) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) obj[0]);  // Secteur
            row.createCell(1).setCellValue((Double) obj[1]);  // Montant
        }

        // ✅ Répartition des investissements par localisation
        rowNum++;
        Row locationHeader = sheet.createRow(rowNum++);
        locationHeader.createCell(0).setCellValue("Investissements par localisation");
        locationHeader.createCell(1).setCellValue("Montant (€)");

        List<Object[]> locationInvestments = investmentRepo.getInvestmentByLocation();
        for (Object[] obj : locationInvestments) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) obj[0]);  // Localisation
            row.createCell(1).setCellValue((Double) obj[1]);  // Montant
        }

        // ✅ Répartition des investissements par risque
        rowNum++;
        Row riskHeader = sheet.createRow(rowNum++);
        riskHeader.createCell(0).setCellValue("Répartition par niveau de risque");
        riskHeader.createCell(1).setCellValue("Montant (€)");

        List<Object[]> riskInvestments = investmentRepo.getInvestmentByRiskCategory();
        for (Object[] obj : riskInvestments) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) obj[0]);  // Catégorie de risque
            row.createCell(1).setCellValue((Double) obj[1]);  // Montant
        }

        // ✅ Top 5 des projets ayant reçu le plus d’investissements
        rowNum++;
        Row projectHeader = sheet.createRow(rowNum++);
        projectHeader.createCell(0).setCellValue("Top 5 Projets Investis");
        projectHeader.createCell(1).setCellValue("Montant Collecté (€)");

        List<Project> topProjects = projectRepo.findTopProjectsByInvestment();
        for (Project p : topProjects) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getTitle());
            row.createCell(1).setCellValue(p.getAmountCollected());
        }

        // ✅ Rendement moyen des investissements (ROI)
        rowNum++;
        Row roiHeader = sheet.createRow(rowNum++);
        roiHeader.createCell(0).setCellValue("ROI moyen des investissements (%)");
        Double avgROI = investmentRepo.getAverageROI();
        roiHeader.createCell(1).setCellValue(avgROI != null ? avgROI : 0.0);

        // ✅ Score ESG moyen des projets financés
//        rowNum++;
//        Row esgHeader = sheet.createRow(rowNum++);
//        esgHeader.createCell(0).setCellValue("Score ESG moyen des projets financés");
//        Double avgESGScore = projectRepo.getAverageESGScore();
//        esgHeader.createCell(1).setCellValue(avgESGScore != null ? avgESGScore : 0.0);

        // ✅ Génération du fichier Excel
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
