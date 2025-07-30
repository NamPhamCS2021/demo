package com.example.demoSQL.util;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.util.Map;

public class PDFExportUtil {
    public static void exportPDF(OutputStream outputStream, Map<String, Object> data) {
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont =  FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Font.BOLD);

        Paragraph title = new Paragraph("Report", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(""));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD);
        table.addCell(new Paragraph("Key", headerFont));
        table.addCell(new Paragraph("Value", headerFont));

        for(Map.Entry<String, Object> entry : data.entrySet()) {
            table.addCell(entry.getKey());
            table.addCell(entry.getValue().toString());
        }
        document.add(table);
        document.close();
    }
}
