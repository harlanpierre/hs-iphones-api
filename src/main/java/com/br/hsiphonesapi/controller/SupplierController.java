package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.dto.request.SupplierRequestDTO;
import com.br.hsiphonesapi.dto.response.SupplierResponseDTO;
import com.br.hsiphonesapi.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/suppliers")
@Tag(name = "Fornecedores", description = "Endpoints para gestão de fornecedores")
public class SupplierController {

    private final SupplierService service;

    @Operation(summary = "Criar novo fornecedor", description = "Cria um fornecedor. O campo CPF/CNPJ é opcional.")
    @ApiResponse(responseCode = "201", description = "Fornecedor criado com sucesso")
    @PostMapping
    public ResponseEntity<SupplierResponseDTO> create(@RequestBody @Valid SupplierRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @Operation(summary = "Listar todos os fornecedores")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<SupplierResponseDTO>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Buscar fornecedor por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornecedor encontrado"),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar fornecedor (Filtro Inteligente)",
            description = "Busca por coincidência no Nome, CPF/CNPJ ou Telefone.")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/search")
    public ResponseEntity<List<SupplierResponseDTO>> search(
            @RequestParam(required = false) String filter) {

        return ResponseEntity.ok(service.findByFilter(filter));
    }

    @Operation(summary = "Atualizar fornecedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornecedor atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> update(@PathVariable Long id, @RequestBody @Valid SupplierRequestDTO dto) {
        try {
            return ResponseEntity.ok(service.update(id, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deletar fornecedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fornecedor deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}