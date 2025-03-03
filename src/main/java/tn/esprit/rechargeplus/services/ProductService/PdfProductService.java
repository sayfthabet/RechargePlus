package tn.esprit.rechargeplus.services.ProductService;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Basket;
import tn.esprit.rechargeplus.entities.Basket_items;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfProductService implements IPdfService {

    @Override
    public byte[] generateBasketPdf(Basket basket) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Basket Receipt"));
            document.add(new Paragraph("Basket ID: " + basket.getIdBasket()));
            document.add(new Paragraph("Total Price: $" + basket.getTotal_price()));

            Table table = new Table(3);
            table.addCell(new Cell().add(new Paragraph("Product Name")));
            table.addCell(new Cell().add(new Paragraph("Quantity")));
            table.addCell(new Cell().add(new Paragraph("Total Price")));

            // Vérification si la liste de produits n'est pas vide
            List<Basket_items> items = basket.getBasket_items();
            if (items != null && !items.isEmpty()) {
                for (Basket_items item : items) {
                    table.addCell(new Cell().add(new Paragraph(item.getProduct().getName())));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
                    table.addCell(new Cell().add(new Paragraph("$" + (item.getQuantity() * item.getProduct().getPrice()))));
                }
            } else {
                // Ajouter une ligne vide si le panier est vide
                table.addCell(new Cell(1, 3).add(new Paragraph("No items in basket")));
            }

            document.add(table);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
