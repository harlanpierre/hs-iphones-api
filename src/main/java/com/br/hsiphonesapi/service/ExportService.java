package com.br.hsiphonesapi.service;

import java.time.LocalDate;

public interface ExportService {

    byte[] exportSalesReport(String format, LocalDate dateFrom, LocalDate dateTo, String status);

    byte[] exportStockReport(String format, String category, Boolean lowStockOnly);

    byte[] exportServiceOrdersReport(String format, LocalDate dateFrom, LocalDate dateTo, String status);

    byte[] exportFinancialReport(String format, LocalDate dateFrom, LocalDate dateTo);
}
