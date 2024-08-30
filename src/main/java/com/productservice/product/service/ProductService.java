package com.productservice.product.service;

import com.productservice.product.dto.request.CreateOptionRequestDTO;
import com.productservice.product.dto.request.CreateProductRequestDTO;
import com.productservice.product.dto.response.OptionDetailResponseDTO;
import com.productservice.product.dto.response.OptionResponseDTO;
import com.productservice.product.dto.response.ProductDetailResponseDTO;
import com.productservice.product.dto.response.ProductImageResponseDTO;
import com.productservice.product.dto.response.ProductListResponseDTO;
import com.productservice.product.dto.response.ProductResponseDTO;
import com.productservice.product.entity.Inventory;
import com.productservice.product.entity.Option;
import com.productservice.product.entity.Product;
import com.productservice.product.entity.ProductImage;
import com.productservice.product.exception.OptionNotFoundException;
import com.productservice.product.exception.ProductNotFoundException;
import com.productservice.product.repository.InventoryRepository;
import com.productservice.product.repository.OptionRepository;
import com.productservice.product.repository.ProductImageRepository;
import com.productservice.product.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final OptionRepository optionRepository;
    private final InventoryRepository inventoryRepository;

    public ProductResponseDTO createProduct(CreateProductRequestDTO requestDTO) {
        Product product = buildAndSaveProduct(requestDTO);

        List<ProductImageResponseDTO> imageResponses = requestDTO.images().stream()
            .map(imageDTO -> saveAndCreateImageResponse(product, imageDTO.url()))
            .collect(Collectors.toList());

        return ProductResponseDTO.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .titleImage(product.getTitleImage())
            .images(imageResponses)
            .build();
    }

    public OptionResponseDTO createOption(Long productId, CreateOptionRequestDTO requestDTO) {
        Product product = productRepository.findById(productId)
            .orElseThrow(ProductNotFoundException::new);

        Option option = saveOption(requestDTO, product);
        Inventory inventory = saveInventory(requestDTO, option);

        return OptionResponseDTO.builder()
            .id(option.getId())
            .type(option.getType())
            .stock(inventory.getStock())
            .build();
    }

    private Product buildAndSaveProduct(CreateProductRequestDTO requestDTO) {
        Product product = Product.builder()
            .name(requestDTO.name())
            .description(requestDTO.description())
            .price(requestDTO.price())
            .titleImage(requestDTO.titleImage())
            .build();
        return productRepository.save(product);
    }

    private ProductImageResponseDTO saveAndCreateImageResponse(Product product, String imageUrl) {
        ProductImage image = ProductImage.builder()
            .url(imageUrl)
            .product(product)
            .build();
        productImageRepository.save(image);
        return ProductImageResponseDTO.builder()
            .id(image.getId())
            .url(image.getUrl())
            .build();
    }

    private Option saveOption(CreateOptionRequestDTO requestDTO, Product product) {
        Option option = Option.builder()
            .type(requestDTO.type())
            .availability(true)
            .product(product)
            .build();
        return optionRepository.save(option);
    }

    private Inventory saveInventory(CreateOptionRequestDTO requestDTO, Option option) {
        Inventory inventory = Inventory.builder()
            .stock(requestDTO.stock())
            .option(option)
            .build();
        return inventoryRepository.save(inventory);
    }

    public List<ProductListResponseDTO> getProductList() {
        return productRepository.findAll().stream()
            .map(product -> ProductListResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .titleImage(product.getTitleImage())
                .build())
            .collect(Collectors.toList());
    }

    public ProductDetailResponseDTO getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException());

        List<ProductImageResponseDTO> imageResponses = product.getProductImages().stream()
            .map(image -> ProductImageResponseDTO.builder()
                .id(image.getId())
                .url(image.getUrl())
                .build())
            .collect(Collectors.toList());

        List<OptionDetailResponseDTO> optionResponses = product.getOptions().stream()
            .map(option -> OptionDetailResponseDTO.builder()
                .id(option.getId())
                .type(option.getType())
                .availability(option.isAvailability())
                .stock(option.getInventory().getStock())
                .build())
            .collect(Collectors.toList());

        return ProductDetailResponseDTO.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .titleImage(product.getTitleImage())
            .productImages(imageResponses)
            .options(optionResponses)
            .build();
    }

    // product to order
    public void updateStock(Long optionId, int additionalStock) {
        // Option을 가져와서 Inventory의 재고를 업데이트
        Option option = optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException());

        Inventory inventory = option.getInventory();
        int currentStock = inventory.getStock();
        int newStock = currentStock + additionalStock;  // 현재 재고에 추가 재고를 더함

        inventory.setStock(newStock);  // 재고 업데이트 및 옵션의 availability 상태가 변경됨
    }
}
