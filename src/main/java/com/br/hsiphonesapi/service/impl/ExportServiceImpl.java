package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.config.tenant.TenantContext;
import com.br.hsiphonesapi.service.ExportService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final JdbcTemplate jdbcTemplate;
    private final TemplateEngine templateEngine;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ===================== SALES REPORT =====================

    @Override
    public byte[] exportSalesReport(String format, LocalDate dateFrom, LocalDate dateTo, String status) {
        Long tenantId = TenantContext.getTenantId();
        LocalDateTime start = dateFrom.atStartOfDay();
        LocalDateTime end = dateTo.plusDays(1).atStartOfDay();

        log.info("Exportando relatório de vendas ({}) para tenant {} de {} a {}", format, tenantId, dateFrom, dateTo);

        StringBuilder sql = new StringBuilder(
                "SELECT s.id, s.created_at, c.name AS client_name, s.seller_name, " +
                "s.total_amount, s.discount_amount, s.net_amount, s.status, " +
                "(SELECT COUNT(*) FROM sale_item si WHERE si.sale_id = s.id) AS item_count, " +
                "(SELECT STRING_AGG(DISTINCT p2.method, ', ') FROM payment p2 WHERE p2.sale_id = s.id) AS payment_methods " +
                "FROM sale s " +
                "LEFT JOIN client c ON s.client_id = c.id " +
                "WHERE s.tenant_id = ? AND s.created_at >= ? AND s.created_at < ?");

        List<Object> params = new ArrayList<>(List.of(tenantId, start, end));

        if (status != null && !status.isBlank()) {
            sql.append(" AND s.status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY s.created_at DESC");

        List<Map<String, Object>> rows = jdbcTemplate.query(sql.toString(), (ResultSet rs, int rowNum) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", rs.getLong("id"));
            row.put("data", rs.getTimestamp("created_at").toLocalDateTime());
            row.put("cliente", rs.getString("client_name"));
            row.put("vendedor", rs.getString("seller_name"));
            row.put("itens", rs.getInt("item_count"));
            row.put("valorBruto", rs.getBigDecimal("total_amount"));
            row.put("desconto", rs.getBigDecimal("discount_amount"));
            row.put("valorLiquido", rs.getBigDecimal("net_amount"));
            row.put("status", rs.getString("status"));
            row.put("formaPagamento", rs.getString("payment_methods"));
            return row;
        }, params.toArray());

        // Calculate totals
        long totalVendas = rows.size();
        BigDecimal totalBruto = rows.stream()
                .map(r -> (BigDecimal) r.get("valorBruto"))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalLiquido = rows.stream()
                .map(r -> (BigDecimal) r.get("valorLiquido"))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalVendas", totalVendas);
        summary.put("totalBruto", totalBruto);
        summary.put("totalLiquido", totalLiquido);

        if ("pdf".equalsIgnoreCase(format)) {
            return generatePdf("reports/sales-report", buildContext(rows, summary, dateFrom, dateTo, status));
        } else {
            String[] headers = {"ID", "Data", "Cliente", "Vendedor", "Itens", "Valor Bruto", "Desconto", "Valor Líquido", "Status", "Forma de Pagamento"};
            List<Object[]> excelRows = rows.stream().map(r -> new Object[]{
                    r.get("id"),
                    r.get("data") != null ? ((LocalDateTime) r.get("data")).format(DATETIME_FMT) : "",
                    r.get("cliente"),
                    r.get("vendedor"),
                    r.get("itens"),
                    r.get("valorBruto"),
                    r.get("desconto"),
                    r.get("valorLiquido"),
                    r.get("status"),
                    r.get("formaPagamento")
            }).toList();

            Object[] footerRow = {"Total: " + totalVendas + " vendas", "", "", "", "", totalBruto, "", totalLiquido, "", ""};
            return generateExcel("Relatório de Vendas", headers, excelRows, footerRow);
        }
    }

    // ===================== STOCK REPORT =====================

    @Override
    public byte[] exportStockReport(String format, String category, Boolean lowStockOnly) {
        Long tenantId = TenantContext.getTenantId();

        log.info("Exportando relatório de estoque ({}) para tenant {}", format, tenantId);

        StringBuilder sql = new StringBuilder(
                "SELECT p.sku, p.name, p.category, p.status, p.quantity, p.min_stock, " +
                "p.purchase_price, p.sale_price, s.name AS supplier_name " +
                "FROM product p " +
                "LEFT JOIN supplier s ON p.supplier_id = s.id " +
                "WHERE p.tenant_id = ? AND p.deleted = false");

        List<Object> params = new ArrayList<>(List.of(tenantId));

        if (category != null && !category.isBlank()) {
            sql.append(" AND p.category = ?");
            params.add(category);
        }
        if (Boolean.TRUE.equals(lowStockOnly)) {
            sql.append(" AND p.quantity < p.min_stock AND p.min_stock IS NOT NULL");
        }
        sql.append(" ORDER BY p.name");

        List<Map<String, Object>> rows = jdbcTemplate.query(sql.toString(), (ResultSet rs, int rowNum) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("sku", rs.getString("sku"));
            row.put("nome", rs.getString("name"));
            row.put("categoria", rs.getString("category"));
            row.put("status", rs.getString("status"));
            row.put("quantidade", rs.getInt("quantity"));
            row.put("estoqueMin", rs.getObject("min_stock") != null ? rs.getInt("min_stock") : null);
            row.put("precoCompra", rs.getBigDecimal("purchase_price"));
            row.put("precoVenda", rs.getBigDecimal("sale_price"));
            row.put("fornecedor", rs.getString("supplier_name"));
            return row;
        }, params.toArray());

        long totalProdutos = rows.size();
        BigDecimal valorTotalEstoque = rows.stream()
                .map(r -> {
                    BigDecimal price = (BigDecimal) r.get("precoCompra");
                    Integer qty = (Integer) r.get("quantidade");
                    if (price != null && qty != null) {
                        return price.multiply(BigDecimal.valueOf(qty));
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalProdutos", totalProdutos);
        summary.put("valorTotalEstoque", valorTotalEstoque);

        if ("pdf".equalsIgnoreCase(format)) {
            Context ctx = new Context();
            ctx.setVariable("rows", rows);
            ctx.setVariable("summary", summary);
            ctx.setVariable("category", category);
            ctx.setVariable("lowStockOnly", lowStockOnly);
            ctx.setVariable("generatedAt", LocalDate.now().format(DATE_FMT));
            return generatePdf("reports/stock-report", ctx);
        } else {
            String[] headers = {"SKU", "Nome", "Categoria", "Status", "Qtd", "Estoque Mín.", "Preço Compra", "Preço Venda", "Fornecedor"};
            List<Object[]> excelRows = rows.stream().map(r -> new Object[]{
                    r.get("sku"),
                    r.get("nome"),
                    r.get("categoria"),
                    r.get("status"),
                    r.get("quantidade"),
                    r.get("estoqueMin"),
                    r.get("precoCompra"),
                    r.get("precoVenda"),
                    r.get("fornecedor")
            }).toList();

            Object[] footerRow = {"Total: " + totalProdutos + " produtos", "", "", "", "", "", valorTotalEstoque, "", ""};
            return generateExcel("Relatório de Estoque", headers, excelRows, footerRow);
        }
    }

    // ===================== SERVICE ORDERS REPORT =====================

    @Override
    public byte[] exportServiceOrdersReport(String format, LocalDate dateFrom, LocalDate dateTo, String status) {
        Long tenantId = TenantContext.getTenantId();
        LocalDateTime start = dateFrom.atStartOfDay();
        LocalDateTime end = dateTo.plusDays(1).atStartOfDay();

        log.info("Exportando relatório de OS ({}) para tenant {} de {} a {}", format, tenantId, dateFrom, dateTo);

        StringBuilder sql = new StringBuilder(
                "SELECT so.id, so.created_at, c.name AS client_name, so.device_model, " +
                "so.device_imei_serial, so.status, so.labor_cost, so.parts_cost, " +
                "so.discount_amount, so.total_amount " +
                "FROM service_order so " +
                "LEFT JOIN client c ON so.client_id = c.id " +
                "WHERE so.tenant_id = ? AND so.created_at >= ? AND so.created_at < ?");

        List<Object> params = new ArrayList<>(List.of(tenantId, start, end));

        if (status != null && !status.isBlank()) {
            sql.append(" AND so.status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY so.created_at DESC");

        List<Map<String, Object>> rows = jdbcTemplate.query(sql.toString(), (ResultSet rs, int rowNum) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", rs.getLong("id"));
            row.put("data", rs.getTimestamp("created_at").toLocalDateTime());
            row.put("cliente", rs.getString("client_name"));
            row.put("dispositivo", rs.getString("device_model"));
            row.put("imeiSerial", rs.getString("device_imei_serial"));
            row.put("status", rs.getString("status"));
            row.put("maoDeObra", rs.getBigDecimal("labor_cost"));
            row.put("pecas", rs.getBigDecimal("parts_cost"));
            row.put("desconto", rs.getBigDecimal("discount_amount"));
            row.put("total", rs.getBigDecimal("total_amount"));
            return row;
        }, params.toArray());

        long totalOS = rows.size();
        BigDecimal receitaTotal = rows.stream()
                .map(r -> (BigDecimal) r.get("total"))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalOS", totalOS);
        summary.put("receitaTotal", receitaTotal);

        if ("pdf".equalsIgnoreCase(format)) {
            return generatePdf("reports/service-orders-report", buildContext(rows, summary, dateFrom, dateTo, status));
        } else {
            String[] headers = {"ID", "Data", "Cliente", "Dispositivo", "IMEI/Serial", "Status", "Mão de Obra", "Peças", "Desconto", "Total"};
            List<Object[]> excelRows = rows.stream().map(r -> new Object[]{
                    r.get("id"),
                    r.get("data") != null ? ((LocalDateTime) r.get("data")).format(DATETIME_FMT) : "",
                    r.get("cliente"),
                    r.get("dispositivo"),
                    r.get("imeiSerial"),
                    r.get("status"),
                    r.get("maoDeObra"),
                    r.get("pecas"),
                    r.get("desconto"),
                    r.get("total")
            }).toList();

            Object[] footerRow = {"Total: " + totalOS + " ordens", "", "", "", "", "", "", "", "", receitaTotal};
            return generateExcel("Relatório de Ordens de Serviço", headers, excelRows, footerRow);
        }
    }

    // ===================== FINANCIAL REPORT =====================

    @Override
    public byte[] exportFinancialReport(String format, LocalDate dateFrom, LocalDate dateTo) {
        Long tenantId = TenantContext.getTenantId();
        LocalDateTime start = dateFrom.atStartOfDay();
        LocalDateTime end = dateTo.plusDays(1).atStartOfDay();

        log.info("Exportando resumo financeiro ({}) para tenant {} de {} a {}", format, tenantId, dateFrom, dateTo);

        // Sales summary (CONCLUIDO only)
        Map<String, Object> salesSummary = jdbcTemplate.queryForMap(
                "SELECT COUNT(*) AS total, COALESCE(SUM(total_amount), 0) AS bruto, " +
                "COALESCE(SUM(discount_amount), 0) AS descontos, COALESCE(SUM(net_amount), 0) AS liquido " +
                "FROM sale WHERE tenant_id = ? AND status = 'CONCLUIDO' AND created_at >= ? AND created_at < ?",
                tenantId, start, end);

        long vendasTotal = ((Number) salesSummary.get("total")).longValue();
        BigDecimal receitaBruta = (BigDecimal) salesSummary.get("bruto");
        BigDecimal descontos = (BigDecimal) salesSummary.get("descontos");
        BigDecimal receitaLiquida = (BigDecimal) salesSummary.get("liquido");

        // Payment methods breakdown
        List<Map<String, Object>> paymentBreakdown = jdbcTemplate.query(
                "SELECT p.method, COUNT(*) AS cnt, COALESCE(SUM(p.amount), 0) AS total " +
                "FROM payment p JOIN sale s ON p.sale_id = s.id " +
                "WHERE s.tenant_id = ? AND s.status = 'CONCLUIDO' AND s.created_at >= ? AND s.created_at < ? " +
                "GROUP BY p.method ORDER BY p.method",
                (ResultSet rs, int rowNum) -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("metodo", rs.getString("method"));
                    row.put("quantidade", rs.getLong("cnt"));
                    row.put("total", rs.getBigDecimal("total"));
                    return row;
                },
                tenantId, start, end);

        // Service orders summary (DELIVERED only)
        Map<String, Object> osSummary = jdbcTemplate.queryForMap(
                "SELECT COUNT(*) AS total, COALESCE(SUM(labor_cost), 0) AS mao_obra, " +
                "COALESCE(SUM(parts_cost), 0) AS pecas, COALESCE(SUM(total_amount), 0) AS receita_total " +
                "FROM service_order WHERE tenant_id = ? AND status = 'DELIVERED' AND created_at >= ? AND created_at < ?",
                tenantId, start, end);

        long osTotal = ((Number) osSummary.get("total")).longValue();
        BigDecimal osMaoObra = (BigDecimal) osSummary.get("mao_obra");
        BigDecimal osPecas = (BigDecimal) osSummary.get("pecas");
        BigDecimal osReceita = (BigDecimal) osSummary.get("receita_total");

        BigDecimal receitaCombinada = receitaLiquida.add(osReceita);

        Map<String, Object> financial = new LinkedHashMap<>();
        financial.put("vendasTotal", vendasTotal);
        financial.put("receitaBruta", receitaBruta);
        financial.put("descontos", descontos);
        financial.put("receitaLiquida", receitaLiquida);
        financial.put("paymentBreakdown", paymentBreakdown);
        financial.put("osTotal", osTotal);
        financial.put("osMaoObra", osMaoObra);
        financial.put("osPecas", osPecas);
        financial.put("osReceita", osReceita);
        financial.put("receitaCombinada", receitaCombinada);

        if ("pdf".equalsIgnoreCase(format)) {
            Context ctx = new Context();
            ctx.setVariable("financial", financial);
            ctx.setVariable("dateFrom", dateFrom.format(DATE_FMT));
            ctx.setVariable("dateTo", dateTo.format(DATE_FMT));
            ctx.setVariable("generatedAt", LocalDate.now().format(DATE_FMT));
            return generatePdf("reports/financial-report", ctx);
        } else {
            return generateFinancialExcel(financial, dateFrom, dateTo);
        }
    }

    // ===================== HELPER METHODS =====================

    private Context buildContext(List<Map<String, Object>> rows, Map<String, Object> summary,
                                LocalDate dateFrom, LocalDate dateTo, String status) {
        Context ctx = new Context();
        ctx.setVariable("rows", rows);
        ctx.setVariable("summary", summary);
        ctx.setVariable("dateFrom", dateFrom.format(DATE_FMT));
        ctx.setVariable("dateTo", dateTo.format(DATE_FMT));
        ctx.setVariable("status", status);
        ctx.setVariable("generatedAt", LocalDate.now().format(DATE_FMT));
        return ctx;
    }

    private byte[] generatePdf(String templateName, Context context) {
        String html = templateEngine.process(templateName, context);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            log.error("Erro ao gerar PDF do relatório: {}", templateName, e);
            throw new RuntimeException("Erro ao gerar relatório PDF", e);
        }
    }

    private byte[] generateExcel(String sheetName, String[] headers, List<Object[]> rows, Object[] footerRow) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName);

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // Currency style
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            currencyStyle.setDataFormat(dataFormat.getFormat("R$ #,##0.00"));

            // Footer style
            CellStyle footerStyle = workbook.createCellStyle();
            Font footerFont = workbook.createFont();
            footerFont.setBold(true);
            footerStyle.setFont(footerFont);
            footerStyle.setBorderTop(BorderStyle.DOUBLE);

            CellStyle footerCurrencyStyle = workbook.createCellStyle();
            footerCurrencyStyle.setFont(footerFont);
            footerCurrencyStyle.setBorderTop(BorderStyle.DOUBLE);
            footerCurrencyStyle.setDataFormat(dataFormat.getFormat("R$ #,##0.00"));

            // Header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowIdx = 1;
            for (Object[] rowData : rows) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < rowData.length; i++) {
                    Cell cell = row.createCell(i);
                    setCellValue(cell, rowData[i], currencyStyle);
                }
            }

            // Footer row
            if (footerRow != null) {
                Row footer = sheet.createRow(rowIdx);
                for (int i = 0; i < footerRow.length; i++) {
                    Cell cell = footer.createCell(i);
                    if (footerRow[i] instanceof BigDecimal bd) {
                        cell.setCellValue(bd.doubleValue());
                        cell.setCellStyle(footerCurrencyStyle);
                    } else if (footerRow[i] != null) {
                        cell.setCellValue(footerRow[i].toString());
                        cell.setCellStyle(footerStyle);
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(os);
            return os.toByteArray();
        } catch (Exception e) {
            log.error("Erro ao gerar planilha Excel", e);
            throw new RuntimeException("Erro ao gerar relatório Excel", e);
        }
    }

    private byte[] generateFinancialExcel(Map<String, Object> financial, LocalDate dateFrom, LocalDate dateTo) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            // Styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);

            CellStyle sectionStyle = workbook.createCellStyle();
            Font sectionFont = workbook.createFont();
            sectionFont.setBold(true);
            sectionFont.setFontHeightInPoints((short) 11);
            sectionStyle.setFont(sectionFont);
            sectionStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            sectionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            DataFormat dataFormat = workbook.createDataFormat();
            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(dataFormat.getFormat("R$ #,##0.00"));

            CellStyle currencyBoldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            currencyBoldStyle.setFont(boldFont);
            currencyBoldStyle.setDataFormat(dataFormat.getFormat("R$ #,##0.00"));

            CellStyle labelBoldStyle = workbook.createCellStyle();
            labelBoldStyle.setFont(boldFont);

            Sheet sheet = workbook.createSheet("Resumo Financeiro");
            int rowIdx = 0;

            // Title
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Resumo Financeiro - " + dateFrom.format(DATE_FMT) + " a " + dateTo.format(DATE_FMT));
            titleCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
            rowIdx++;

            // ---- VENDAS section ----
            Row vendasHeader = sheet.createRow(rowIdx++);
            Cell vendasCell = vendasHeader.createCell(0);
            vendasCell.setCellValue("Vendas");
            vendasCell.setCellStyle(sectionStyle);
            vendasHeader.createCell(1).setCellStyle(sectionStyle);
            vendasHeader.createCell(2).setCellStyle(sectionStyle);

            addFinancialRow(sheet, rowIdx++, "Total de vendas concluídas", financial.get("vendasTotal"), null, null);
            addFinancialRow(sheet, rowIdx++, "Receita bruta", null, (BigDecimal) financial.get("receitaBruta"), currencyStyle);
            addFinancialRow(sheet, rowIdx++, "Descontos", null, (BigDecimal) financial.get("descontos"), currencyStyle);
            addFinancialRow(sheet, rowIdx++, "Receita líquida", null, (BigDecimal) financial.get("receitaLiquida"), currencyBoldStyle);
            rowIdx++;

            // ---- FORMAS DE PAGAMENTO section ----
            Row pagHeader = sheet.createRow(rowIdx++);
            Cell pagCell = pagHeader.createCell(0);
            pagCell.setCellValue("Formas de Pagamento");
            pagCell.setCellStyle(sectionStyle);
            pagHeader.createCell(1).setCellStyle(sectionStyle);
            pagHeader.createCell(2).setCellStyle(sectionStyle);

            Row pagColHeaders = sheet.createRow(rowIdx++);
            pagColHeaders.createCell(0).setCellValue("Método");
            pagColHeaders.createCell(1).setCellValue("Quantidade");
            pagColHeaders.createCell(2).setCellValue("Total");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> paymentBreakdown = (List<Map<String, Object>>) financial.get("paymentBreakdown");
            for (Map<String, Object> pm : paymentBreakdown) {
                Row pmRow = sheet.createRow(rowIdx++);
                pmRow.createCell(0).setCellValue(pm.get("metodo").toString());
                pmRow.createCell(1).setCellValue(((Number) pm.get("quantidade")).longValue());
                Cell totalCell = pmRow.createCell(2);
                totalCell.setCellValue(((BigDecimal) pm.get("total")).doubleValue());
                totalCell.setCellStyle(currencyStyle);
            }
            rowIdx++;

            // ---- ASSISTENCIA TECNICA section ----
            Row osHeader = sheet.createRow(rowIdx++);
            Cell osCell = osHeader.createCell(0);
            osCell.setCellValue("Assistência Técnica");
            osCell.setCellStyle(sectionStyle);
            osHeader.createCell(1).setCellStyle(sectionStyle);
            osHeader.createCell(2).setCellStyle(sectionStyle);

            addFinancialRow(sheet, rowIdx++, "Total de OS entregues", financial.get("osTotal"), null, null);
            addFinancialRow(sheet, rowIdx++, "Receita mão de obra", null, (BigDecimal) financial.get("osMaoObra"), currencyStyle);
            addFinancialRow(sheet, rowIdx++, "Receita peças", null, (BigDecimal) financial.get("osPecas"), currencyStyle);
            addFinancialRow(sheet, rowIdx++, "Receita total OS", null, (BigDecimal) financial.get("osReceita"), currencyBoldStyle);
            rowIdx++;

            // ---- RESUMO section ----
            Row resumoHeader = sheet.createRow(rowIdx++);
            Cell resumoCell = resumoHeader.createCell(0);
            resumoCell.setCellValue("Resumo Geral");
            resumoCell.setCellStyle(sectionStyle);
            resumoHeader.createCell(1).setCellStyle(sectionStyle);
            resumoHeader.createCell(2).setCellStyle(sectionStyle);

            Row combinadaRow = sheet.createRow(rowIdx);
            Cell label = combinadaRow.createCell(0);
            label.setCellValue("Receita total combinada (Vendas + OS)");
            label.setCellStyle(labelBoldStyle);
            Cell valCell = combinadaRow.createCell(1);
            valCell.setCellValue(((BigDecimal) financial.get("receitaCombinada")).doubleValue());
            valCell.setCellStyle(currencyBoldStyle);

            // Auto-size
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);

            workbook.write(os);
            return os.toByteArray();
        } catch (Exception e) {
            log.error("Erro ao gerar planilha financeira Excel", e);
            throw new RuntimeException("Erro ao gerar relatório financeiro Excel", e);
        }
    }

    private void addFinancialRow(Sheet sheet, int rowIdx, String label, Object countValue, BigDecimal currencyValue, CellStyle style) {
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(label);
        if (countValue != null) {
            row.createCell(1).setCellValue(((Number) countValue).longValue());
        }
        if (currencyValue != null) {
            Cell cell = row.createCell(1);
            cell.setCellValue(currencyValue.doubleValue());
            if (style != null) cell.setCellStyle(style);
        }
    }

    private void setCellValue(Cell cell, Object value, CellStyle currencyStyle) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof BigDecimal bd) {
            cell.setCellValue(bd.doubleValue());
            cell.setCellStyle(currencyStyle);
        } else if (value instanceof Number num) {
            cell.setCellValue(num.doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
