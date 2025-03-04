package tn.esprit.rechargeplus.controllers.InvestmentController;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.rechargeplus.services.InvestmentServices.InvestmentStatisticsService;

import java.io.IOException;

@RestController
@RequestMapping("/statistics")
public class InvestmentStatisticsController {

    private final InvestmentStatisticsService statisticsService;

    public InvestmentStatisticsController(InvestmentStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/downloadExcel")
    public ResponseEntity<byte[]> downloadInvestmentStatistics() throws IOException {
        byte[] excelData = statisticsService.generateInvestmentStatisticsExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=investment_statistics.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
}
