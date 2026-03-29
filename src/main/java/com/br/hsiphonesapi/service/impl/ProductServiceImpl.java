package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.dto.request.ProductRequestDTO;
import com.br.hsiphonesapi.dto.response.ProductResponseDTO;
import com.br.hsiphonesapi.mapper.ProductMapper;
import com.br.hsiphonesapi.model.Client;
import com.br.hsiphonesapi.model.Product;
import com.br.hsiphonesapi.model.Supplier;
import com.br.hsiphonesapi.model.enums.ProductCategory;
import com.br.hsiphonesapi.model.enums.ProductStatus;
import com.br.hsiphonesapi.repository.ClientRepository;
import com.br.hsiphonesapi.repository.ProductRepository;
import com.br.hsiphonesapi.repository.SupplierRepository;
import com.br.hsiphonesapi.service.PlanUsageService;
import com.br.hsiphonesapi.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ClientRepository clientRepository;
    private final ProductMapper mapper;
    private final PlanUsageService planUsageService;

    @Override
    @Transactional
    public ProductResponseDTO save(ProductRequestDTO dto) {
        planUsageService.checkCanCreateProduct();

        // 1. Validação de IMEI Obrigatório para Celular (Lista não pode ser vazia)
        if (dto.getCategory() == ProductCategory.CELULAR) {
            if (dto.getImeis() == null || dto.getImeis().isEmpty()) {
                throw new IllegalArgumentException("É necessário informar ao menos um IMEI para Celulares.");
            }
        }

        // --- 2. Validação de Origem (Fornecedor OU Cliente) ---
        if (dto.getSupplierId() == null && dto.getClientId() == null) {
            throw new IllegalArgumentException("É necessário informar a origem do produto (Fornecedor ou Cliente).");
        }

        // --- 3. Geração de SKU ---
        if (dto.getSku() == null || dto.getSku().isBlank()) {
            dto.setSku(generateDynamicSku(dto));
        } else if (productRepository.existsBySku(dto.getSku())) {
            throw new IllegalArgumentException("Já existe um produto com este SKU.");
        }

        // --- 4. Validação de BuyBack (Verifica se ALGUM dos IMEIs já está disponivel) ---
        if (dto.getImeis() != null && !dto.getImeis().isEmpty()) {
            if (productRepository.existsByAnyImeiAndStatusAvailable(dto.getImeis())) {
                throw new IllegalArgumentException("Um dos IMEIs informados já consta no estoque como DISPONÍVEL.");
            }
        }

        Product product = mapper.toEntity(dto);
        product.setStatus(ProductStatus.DISPONIVEL);

        // --- 5. Vínculo de Origem ---
        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor não encontrado."));
            product.setSupplier(supplier);
        } else if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
            product.setClient(client);
        }

        Product saved = productRepository.save(product);
        log.info("Produto cadastrado: id={}, sku='{}', categoria={}", saved.getId(), saved.getSku(), saved.getCategory());
        return mapper.toResponse(saved);
    }

    // Lógica Auxiliar para Gerar SKU
    private String generateDynamicSku(ProductRequestDTO dto) {
        // Exemplo: CATEGORIA (3 letras) + NOME (3 letras) + TIMESTAMP (ultimos 6 digitos)
        // Resultado: CEL-IPH-839201

        String catPart = dto.getCategory().name().substring(0, Math.min(dto.getCategory().name().length(), 3));

        String namePart = dto.getName().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        namePart = namePart.substring(0, Math.min(namePart.length(), 3));

        String timePart = String.valueOf(System.currentTimeMillis());
        timePart = timePart.substring(timePart.length() - 6);

        return String.format("%s-%s-%s", catPart, namePart, timePart);
    }

    @Override
    public List<ProductResponseDTO> findHistoryByImei(String imei) {
        return productRepository.findByImeiHistory(imei).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public Page<ProductResponseDTO> findAvailableProducts(Pageable pageable) {
        return productRepository.findByStatus(ProductStatus.DISPONIVEL, pageable).map(mapper::toResponse);
    }

    @Override
    public Page<ProductResponseDTO> findByCategoryAndStatus(ProductCategory category, ProductStatus status, Pageable pageable) {
        return productRepository.findByCategoryAndStatus(category, status, pageable).map(mapper::toResponse);
    }

    @Override
    public ProductResponseDTO findById(Long id) {
        return productRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        // Atualiza campos simples
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPurchasePrice(dto.getPurchasePrice());
        existing.setSalePrice(dto.getSalePrice());
        existing.setQuantity(dto.getQuantity());
        existing.setMinStock(dto.getMinStock());
        existing.setCompatibleModel(dto.getCompatibleModel());

        // Lógica para datas de garantia
        existing.setSupplierWarrantyStartDate(dto.getSupplierWarrantyStartDate());
        existing.setSupplierWarrantyEndDate(dto.getSupplierWarrantyEndDate());

        // Se mudou o fornecedor
        if (!existing.getSupplier().getId().equals(dto.getSupplierId())) {
            Supplier newSupplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new EntityNotFoundException("Novo fornecedor não encontrado."));
            existing.setSupplier(newSupplier);
        }

        Product updated = productRepository.save(existing);
        log.info("Produto atualizado: id={}, sku='{}'", updated.getId(), updated.getSku());
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));
        product.setDeleted(true);
        productRepository.save(product);
        log.info("Produto removido (soft delete): id={}", id);
    }
}