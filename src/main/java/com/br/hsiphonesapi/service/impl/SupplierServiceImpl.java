package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.dto.request.SupplierRequestDTO;
import com.br.hsiphonesapi.dto.response.SupplierResponseDTO;
import com.br.hsiphonesapi.mapper.SupplierMapper;
import com.br.hsiphonesapi.model.Supplier;
import com.br.hsiphonesapi.repository.SupplierRepository;
import com.br.hsiphonesapi.service.SupplierService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository repository;
    private final SupplierMapper mapper;

    @Override
    @Transactional
    public SupplierResponseDTO save(SupplierRequestDTO dto) {
        // 1. Limpeza e Validação de Duplicidade (CPF/CNPJ)
        if (dto.getCpfCnpj() != null && !dto.getCpfCnpj().isBlank()) {
            // Remove tudo que não for número
            String cleanCpfCnpj = dto.getCpfCnpj().replaceAll("\\D", "");

            // Atualiza o DTO para garantir que o Mapper salve apenas números no banco
            dto.setCpfCnpj(cleanCpfCnpj);

            if (repository.existsByCpfCnpj(cleanCpfCnpj)) {
                throw new IllegalArgumentException("Fornecedor já cadastrado com este CPF/CNPJ.");
            }
        }

        // 2. Validação de Telefone
        // (Sugestão: Também limpar o telefone se quiser salvar padrão, ex: apenas números)
        if (repository.existsByPhone(dto.getPhone())) {
            throw new IllegalArgumentException("Fornecedor já cadastrado com este Telefone.");
        }

        Supplier supplier = mapper.toEntity(dto);
        Supplier savedSupplier = repository.save(supplier);
        return mapper.toResponse(savedSupplier);
    }

    @Override
    public List<SupplierResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor não encontrado."));
    }

    @Override
    public List<SupplierResponseDTO> findByFilter(String filter) {
        if (filter == null || filter.isBlank()) {
            return findAll();
        }

        String cleanFilter = filter;

        // Verifica se tem números E (pontos OU traços OU parênteses OU barras)
        // Adicionei o \\/ dentro dos colchetes [ ... ]
        if (filter.matches(".*\\d.*") && filter.matches(".*[\\.\\-\\(\\)\\/].*")) {

            cleanFilter = filter.replaceAll("\\D", "");

            // Se a limpeza resultou em vazio (ex: usuário digitou apenas "./-"),
            // restauramos o original para tentar buscar por nome
            if (cleanFilter.isBlank()) {
                cleanFilter = filter;
            }
        }

        return repository.findByFilter(cleanFilter).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SupplierResponseDTO update(Long id, SupplierRequestDTO dto) {
        Supplier existingSupplier = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor não encontrado."));

        // 2. Validação de Telefone no Update (Verifica se mudou e se já existe em OUTRO registro)
        if (!existingSupplier.getPhone().equals(dto.getPhone())) {
            if (repository.existsByPhone(dto.getPhone())) {
                throw new IllegalArgumentException("Este telefone já está em uso por outro fornecedor.");
            }
        }

        // Atualiza apenas Nome e Telefone
        existingSupplier.setName(dto.getName());
        existingSupplier.setPhone(dto.getPhone());

        // OBS: O CPF/CNPJ NÃO é atualizado aqui (Mantém o existingSupplier.getCpfCnpj())

        return mapper.toResponse(repository.save(existingSupplier));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Fornecedor não encontrado.");
        }
        repository.deleteById(id);
    }
}