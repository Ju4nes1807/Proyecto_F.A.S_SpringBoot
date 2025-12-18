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
import com.fas.project.dto.torneo.TorneoReporteDTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;

@Service
public class ReporteService {
    
    // ===== REPORTES DE JUGADORES =====
    
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
        try (Workbook workbook = new XSSFWorkbook()) {
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
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 500);
            }

            workbook.write(outputStream);
        }
    }
    
    // ===== REPORTES DE TORNEOS =====
    
    public void generarReporteTorneosPDF(List<TorneoReporteDTO> torneos, OutputStream outputStream) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Título del reporte
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Reporte de Torneos", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Fecha de generación
        com.itextpdf.text.Font dateFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL, BaseColor.GRAY);
        Paragraph date = new Paragraph("Fecha de generación: " + 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), 
            dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(15);
        document.add(date);

        // Crear tabla con 7 columnas
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Configurar anchos de columnas
        float[] columnWidths = {3f, 2f, 2f, 2f, 2f, 2f, 2f};
        table.setWidths(columnWidths);

        // Encabezados de la tabla
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
        String[] headers = {"Nombre", "Categoría", "Fecha Inicio", "Fecha Fin", "Estado", "Inscritos", "Cupos"};
        
        for (String header : headers) {
            com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new Paragraph(header, headerFont));
            cell.setBackgroundColor(new BaseColor(41, 128, 185)); // Azul
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        // Datos de la tabla
        com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.NORMAL);
        for (TorneoReporteDTO torneo : torneos) {
            table.addCell(new Paragraph(torneo.getNombre(), cellFont));
            table.addCell(new Paragraph(torneo.getCategoriaNombre(), cellFont));
            table.addCell(new Paragraph(torneo.getFechaInicio(), cellFont));
            table.addCell(new Paragraph(torneo.getFechaFin(), cellFont));
            table.addCell(new Paragraph(torneo.getEstado(), cellFont));
            table.addCell(new Paragraph(String.valueOf(torneo.getTotalInscritos()), cellFont));
            table.addCell(new Paragraph(torneo.getCuposDisponibles() + "/" + torneo.getCupoMaximo(), cellFont));
        }

        document.add(table);

        // Pie de página con total
        Paragraph total = new Paragraph("Total de torneos: " + torneos.size(), 
            new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD));
        total.setSpacingBefore(15);
        document.add(total);

        document.close();
    }

    public void generarReporteTorneosExcel(List<TorneoReporteDTO> torneos, OutputStream outputStream) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Torneos");

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
            String[] columnas = { "ID", "Nombre", "Categoría", "Fecha Inicio", "Fecha Fin", 
                                 "Ubicación", "Estado", "Inscritos", "Cupos Disponibles", "Cupo Máximo", "Descripción" };

            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar los datos
            int rowNum = 1;
            for (TorneoReporteDTO torneo : torneos) {
                Row row = sheet.createRow(rowNum++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(torneo.getIdTorneo());
                cell0.setCellStyle(dataStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(torneo.getNombre());
                cell1.setCellStyle(dataStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(torneo.getCategoriaNombre());
                cell2.setCellStyle(dataStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(torneo.getFechaInicio());
                cell3.setCellStyle(dataStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(torneo.getFechaFin());
                cell4.setCellStyle(dataStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(torneo.getUbicacionLocalidad() + " - " + torneo.getUbicacionBarrio());
                cell5.setCellStyle(dataStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(torneo.getEstado());
                cell6.setCellStyle(dataStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(torneo.getTotalInscritos());
                cell7.setCellStyle(dataStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(torneo.getCuposDisponibles());
                cell8.setCellStyle(dataStyle);

                Cell cell9 = row.createCell(9);
                cell9.setCellValue(torneo.getCupoMaximo());
                cell9.setCellStyle(dataStyle);

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(torneo.getDescripcion() != null ? torneo.getDescripcion() : "");
                cell10.setCellStyle(dataStyle);
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 500);
            }

            // Escribir el archivo
            workbook.write(outputStream);
        }
    }
}