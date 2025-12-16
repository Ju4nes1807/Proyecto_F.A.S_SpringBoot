package com.fas.project.service;

import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.fas.project.dto.user.JugadorDTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class ReporteService {
    public void generarReporteJugadoresPDF(List<JugadorDTO> jugadores, OutputStream outputStream) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        document.add(new Paragraph("Reporte de Jugadores"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.addCell("Nombre");
        table.addCell("Email");
        table.addCell("Categoria");
        table.addCell("Escuela");
        table.addCell("Posicion");
        table.addCell("Dorsal");

        for (JugadorDTO j : jugadores) {
            table.addCell(j.getNombres() + " " + j.getApellidos());
            table.addCell(j.getEmail());
            table.addCell(j.getNombreCategoria());
            table.addCell(j.getNombreEscuela());
            table.addCell(j.getPosicion());
            table.addCell(String.valueOf(j.getNumeroCamiseta()));
        }

        document.add(table);
        document.close();
    }

    public void generarReporteJugadoresExcel(List<JugadorDTO> jugadores, OutputStream outputStream) throws Exception {
        try ( // Crear el workbook y la hoja
                Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Jugadores");

            // Crear estilo para el encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Crear estilo para las celdas de datos
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Crear fila de encabezado
            Row headerRow = sheet.createRow(0);
            String[] columnas = { "Nombre Completo", "Email", "Documento", "Teléfono",
                    "Fecha Nacimiento", "Categoría", "Escuela", "Posición", "Dorsal" };

            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar los datos
            int rowNum = 1;
            for (JugadorDTO jugador : jugadores) {
                Row row = sheet.createRow(rowNum++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(jugador.getNombres() + " " + jugador.getApellidos());
                cell0.setCellStyle(dataStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(jugador.getEmail());
                cell1.setCellStyle(dataStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(jugador.getNumDocumento() != null ? jugador.getNumDocumento().toString() : "");
                cell2.setCellStyle(dataStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(jugador.getTelefono());
                cell3.setCellStyle(dataStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(jugador.getFechaNacimiento());
                cell4.setCellStyle(dataStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(jugador.getNombreCategoria());
                cell5.setCellStyle(dataStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(jugador.getNombreEscuela());
                cell6.setCellStyle(dataStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(jugador.getPosicion());
                cell7.setCellStyle(dataStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(jugador.getNumeroCamiseta());
                cell8.setCellStyle(dataStyle);
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
                // Agregar un poco más de espacio
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 500);
            }

            // Escribir el archivo
            workbook.write(outputStream);
        }
    }
}
