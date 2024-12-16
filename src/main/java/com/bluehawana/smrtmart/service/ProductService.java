package com.bluehawana.smrtmart.service;

import com.bluehawana.smrtmart.dto.ProductDTO;
import com.bluehawana.smrtmart.exception.ResourceNotFoundException;
import com.bluehawana.smrtmart.model.Product;
import com.bluehawana.smrtmart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProduct(Integer id) {
        Product product = productRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Integer id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        updateProductFields(existingProduct, productDTO);
        Product savedProduct = productRepository.save(existingProduct);
        return convertToDTO(savedProduct);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(Long.valueOf(id))) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(Long.valueOf(id));
    }

    private void updateProductFields(Product product, ProductDTO dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setStockQuantity(dto.getStockQuantity());
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(Math.toIntExact(Long.parseLong(product.getId())));
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setStockQuantity(product.getStockQuantity());
        return dto;
    }

    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setStockQuantity(dto.getStockQuantity());
        return product;
    }
}
