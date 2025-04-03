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

            // Check if basket is populated
            System.out.println("Basket ID: " + basket.getIdBasket());
            System.out.println("Basket Total Price: " + basket.getTotal_price());
            List<Basket_items> items = basket.getBasket_items();
            System.out.println("Basket Items Count: " + (items != null ? items.size() : 0));

            // Add Basket Header
            document.add(new Paragraph("Basket Receipt"));
            document.add(new Paragraph("Basket ID: " + basket.getIdBasket()));
            document.add(new Paragraph("Total Price: $" + basket.getTotal_price()));

            // Create a table for product information
            Table table = new Table(3);
            table.addCell(new Cell().add(new Paragraph("Product Name")));
            table.addCell(new Cell().add(new Paragraph("Quantity")));
            table.addCell(new Cell().add(new Paragraph("Total Price")));

            // Check if the basket contains items
            if (items != null && !items.isEmpty()) {
                for (Basket_items item : items) {
                    // Ensure Product data is present
                    if (item.getProduct() != null) {
                        System.out.println("Product Name: " + item.getProduct().getName());
                        System.out.println("Quantity: " + item.getQuantity());
                        System.out.println("Product Price: " + item.getProduct().getPrice());
                        table.addCell(new Cell().add(new Paragraph(item.getProduct().getName())));
                        table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
                        table.addCell(new Cell().add(new Paragraph("$" + (item.getQuantity() * item.getProduct().getPrice()))));
                    } else {
                        System.out.println("Product is null for item ID: " + item.getIdBasket_items());
                        table.addCell(new Cell(1, 3).add(new Paragraph("Product data missing")));
                    }
                }
            } else {
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
