package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Relatórios", description = "Exportação de relatórios em PDF e Excel")
public class ReportController {

    private final ExportService exportService;

    private static final DateTimeFormatter FILE_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/sales")
    @Operation(summary = "Relatório de Vendas")
    public ResponseEntity<byte[]> exportSales(
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String status) {

        byte[] data = exportService.exportSalesReport(format, dateFrom, dateTo, status);
        String filename = "relatorio-vendas_" + dateFrom.format(FILE_DATE_FMT) + "_" + dateTo.format(FILE_DATE_FMT);
        return buildResponse(data, format, filename);
    }

    @GetMapping("/stock")
    @Operation(summary = "Relatório de Estoque")
    public ResponseEntity<byte[]> exportStock(
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean lowStockOnly) {

        byte[] data = exportService.exportStockReport(format, category, lowStockOnly);
        String filename = "relatorio-estoque_" + LocalDate.now().format(FILE_DATE_FMT);
        return buildResponse(data, format, filename);
    }

    @GetMapping("/service-orders")
    @Operation(summary = "Relatório de Ordens de Serviço")
    public ResponseEntity<byte[]> exportServiceOrders(
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String status) {

        byte[] data = exportService.exportServiceOrdersReport(format, dateFrom, dateTo, status);
        String filename = "relatorio-os_" + dateFrom.format(FILE_DATE_FMT) + "_" + dateTo.format(FILE_DATE_FMT);
        return buildResponse(data, format, filename);
    }

    @GetMapping("/financial")
    @Operation(summary = "Resumo Financeiro")
    public ResponseEntity<byte[]> exportFinancial(
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        byte[] data = exportService.exportFinancialReport(format, dateFrom, dateTo);
        String filename = "resumo-financeiro_" + dateFrom.format(FILE_DATE_FMT) + "_" + dateTo.format(FILE_DATE_FMT);
        return buildResponse(data, format, filename);
    }

    private ResponseEntity<byte[]> buildResponse(byte[] data, String format, String filename) {
        String ext = "pdf".equalsIgnoreCase(format) ? ".pdf" : ".xlsx";
        MediaType contentType = "pdf".equalsIgnoreCase(format)
                ? MediaType.APPLICATION_PDF
                : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + ext + "\"")
                .contentType(contentType)
                .body(data);
    }
}
