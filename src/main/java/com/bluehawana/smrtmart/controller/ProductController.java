package com.bluehawana.smrtmart.controller;

import com.bluehawana.smrtmart.dto.ApiResponse;
import com.bluehawana.smrtmart.dto.ProductDTO;
import com.bluehawana.smrtmart.exception.ResourceNotFoundException;
import com.bluehawana.smrtmart.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("products/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Integer id) {
        try {
            ProductDTO product = productService.getProduct(id);
            return ResponseEntity.ok(new ApiResponse("Product found", true, product));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse("Product not found", false, null));
        }
    }

    @PostMapping("products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        try {
            ProductDTO createdProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(new ApiResponse("Product created successfully", true, createdProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to create product: " + e.getMessage(), false, null));
        }
    }

    @PutMapping("products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id,
                                           @Valid @RequestBody ProductDTO productDTO) {
        try {
            ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(new ApiResponse("Product updated successfully", true, updatedProduct));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse("Product not found", false, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update product: " + e.getMessage(), false, null));
        }
    }

    @DeleteMapping("products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(new ApiResponse("Product deleted successfully", true, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse("Product not found", false, null));
        }
    }
}