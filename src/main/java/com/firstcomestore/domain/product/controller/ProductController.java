package com.firstcomestore.domain.product.controller;

import com.firstcomestore.common.dto.ResponseDTO;
import com.firstcomestore.domain.product.dto.request.CreateOptionRequestDTO;
import com.firstcomestore.domain.product.dto.request.CreateProductRequestDTO;
import com.firstcomestore.domain.product.dto.response.OptionResponseDTO;
import com.firstcomestore.domain.product.dto.response.ProductDetailResponseDTO;
import com.firstcomestore.domain.product.dto.response.ProductListResponseDTO;
import com.firstcomestore.domain.product.dto.response.ProductResponseDTO;
import com.firstcomestore.domain.product.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/admin/products")
    public ResponseEntity<ResponseDTO<ProductResponseDTO>> createProduct(
        @Valid @RequestBody CreateProductRequestDTO requestDTO) {
        ProductResponseDTO response = productService.createProduct(requestDTO);
        return ResponseEntity.ok(ResponseDTO.okWithData(response));
    }

    @PostMapping("/admin/options/{productId}")
    public ResponseEntity<ResponseDTO<OptionResponseDTO>> createOption(@PathVariable Long productId,
        @Valid @RequestBody CreateOptionRequestDTO requestDTO) {
        OptionResponseDTO response = productService.createOption(productId, requestDTO);
        return ResponseEntity.ok(ResponseDTO.okWithData(response));
    }

    @GetMapping("/products")
    public ResponseEntity<ResponseDTO<List<ProductListResponseDTO>>> getProductList() {
        List<ProductListResponseDTO> response = productService.getProductList();
        return ResponseEntity.ok(ResponseDTO.okWithData(response));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ResponseDTO<ProductDetailResponseDTO>> getProductDetail(
        @PathVariable Long productId) {
        ProductDetailResponseDTO response = productService.getProductDetail(productId);
        return ResponseEntity.ok(ResponseDTO.okWithData(response));
    }
}
