package com.productservice.product.controller;

import com.productservice.common.dto.ResponseDTO;
import com.productservice.product.dto.request.CreateOptionRequestDTO;
import com.productservice.product.dto.request.CreateProductRequestDTO;
import com.productservice.product.dto.request.UpdateMaxPurchaseLimitRequestDTO;
import com.productservice.product.dto.response.OptionDetailDTO;
import com.productservice.product.dto.response.OptionResponseDTO;
import com.productservice.product.dto.response.ProductDetailResponseDTO;
import com.productservice.product.dto.response.ProductListResponseDTO;
import com.productservice.product.dto.response.ProductResponseDTO;
import com.productservice.product.entity.Option;
import com.productservice.product.entity.Product;
import com.productservice.product.exception.OptionNotFoundException;
import com.productservice.product.repository.OptionRepository;
import com.productservice.product.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final OptionRepository optionRepository;

    @PostMapping("/admin/products")
    public ResponseEntity<ResponseDTO<ProductResponseDTO>> createProduct(
        @Valid @RequestBody CreateProductRequestDTO requestDTO) {
        ProductResponseDTO response = productService.createProduct(requestDTO);
        return ResponseEntity.ok(ResponseDTO.okWithData(response));
    }

    @PostMapping("/admin/products/{productId}/options")
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

    @PutMapping("/admin/products/options/{optionId}/max-purchase-limit")
    public ResponseEntity<Void> updateMaxPurchaseLimit(
        @PathVariable Long optionId,
        @Valid @RequestBody UpdateMaxPurchaseLimitRequestDTO request) {

        productService.updateMaxPurchaseLimit(optionId, request.maxPurchaseLimit());
        return ResponseEntity.ok().build();
    }

    //product to order
    @GetMapping("/product-service/options/{optionId}/exists")
    public ResponseEntity<Boolean> checkOptionExists(@PathVariable Long optionId) {
        boolean exists = optionRepository.existsById(optionId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/product-service/options/{optionId}/inventory")
    public ResponseEntity<Integer> getOptionStock(@PathVariable Long optionId) {
        Option option = optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException());
        return ResponseEntity.ok(option.getInventory().getStock());
    }

    @GetMapping("/product-service/options/{optionId}/details")
    public ResponseEntity<OptionDetailDTO> getOptionDetails(
        @PathVariable("optionId") Long optionId) {
        Option option = optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException());

        Product product = option.getProduct();

        OptionDetailDTO optionDetailDTO = OptionDetailDTO.builder()
            .optionId(option.getId())
            .optionType(option.getType())
            .availability(option.isAvailability())
            .stock(option.getInventory().getStock())
            .productId(product.getId())
            .productName(product.getName())
            .build();

        return ResponseEntity.ok(optionDetailDTO);
    }

    @PutMapping("/product-service/options/{optionId}/inventory")
    public ResponseEntity<Void> updateOptionStock(@PathVariable Long optionId, @RequestBody int stock) {
        productService.updateStock(optionId, stock);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product-service/options/{optionId}/max-purchase-limit")
    public ResponseEntity<Integer> getMaxPurchaseLimit(@PathVariable Long optionId) {
        Integer maxPurchaseLimit = productService.getMaxPurchaseLimit(optionId);
        return ResponseEntity.ok(maxPurchaseLimit);
    }
}
