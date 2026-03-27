package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.dto.request.ProductRequestDTO;
import com.br.hsiphonesapi.dto.response.ProductResponseDTO;
import com.br.hsiphonesapi.model.enums.ProductCategory;
import com.br.hsiphonesapi.model.enums.ProductStatus;
import com.br.hsiphonesapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "Produtos", description = "Gestão de inventário (Celulares, Peças e Acessórios)")
public class ProductController {

    private final ProductService service;

    @PostMapping
    @Operation(summary = "Cadastrar produto")
    public ResponseEntity<ProductResponseDTO> create(@RequestBody @Valid ProductRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos os produtos (paginado)")
    public ResponseEntity<Page<ProductResponseDTO>> listAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/available")
    @Operation(summary = "Listar produtos disponíveis para venda (paginado)")
    public ResponseEntity<Page<ProductResponseDTO>> getAvailableProducts(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.findAvailableProducts(pageable));
    }

    @GetMapping("/filter")
    @Operation(summary = "Buscar produtos por Categoria e Status (paginado)",
            description = "Retorna uma lista paginada filtrada pela categoria (ex: CELULAR, PECA) e pelo status atual (ex: DISPONIVEL, IN_REPAIR).")
    public ResponseEntity<Page<ProductResponseDTO>> findByCategoryAndStatus(
            @RequestParam ProductCategory category,
            @RequestParam ProductStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.findByCategoryAndStatus(category, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Histórico de IMEI (BuyBack)",
            description = "Retorna todas as vezes que este IMEI passou pela loja (Entradas e Saídas).")
    @GetMapping("/history/{imei}")
    public ResponseEntity<List<ProductResponseDTO>> getImeiHistory(@PathVariable String imei) {
        return ResponseEntity.ok(service.findHistoryByImei(imei));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ProductRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar produto")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
