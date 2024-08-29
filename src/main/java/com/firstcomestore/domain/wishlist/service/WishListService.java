package com.firstcomestore.domain.wishlist.service;

import com.firstcomestore.common.feignclient.ProductServiceClient;
import com.firstcomestore.domain.order.exception.InsufficientStockException;
import com.firstcomestore.domain.wishlist.dto.request.AddWishListRequestDTO;
import com.firstcomestore.domain.wishlist.dto.response.OptionDetailDTO;
import com.firstcomestore.domain.wishlist.dto.response.WishListResponseDTO;
import com.firstcomestore.domain.wishlist.entity.WishList;
import com.firstcomestore.domain.wishlist.exception.OptionNotFoundException;
import com.firstcomestore.domain.wishlist.exception.WishNotFoundException;
import com.firstcomestore.domain.wishlist.repository.WishListRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WishListService {

    private final WishListRepository wishRepository;
    private final ProductServiceClient productServiceClient;

    public void addToWishList(Long userId, Long optionId, AddWishListRequestDTO requestDTO) {
        ResponseEntity<Boolean> response = productServiceClient.checkOptionExists(optionId);
        if (!response.getBody()) {
            throw new OptionNotFoundException();
        }

        ResponseEntity<Integer> stockResponse = productServiceClient.getOptionStock(optionId);
        int availableStock = stockResponse.getBody();
        if (availableStock < requestDTO.quantity()) {
            throw new InsufficientStockException();
        }

        WishList existingWish = wishRepository.findByUserIdAndOptionId(userId, optionId);
        if (existingWish != null) {
            existingWish.setQuantity(existingWish.getQuantity() + requestDTO.quantity());
            wishRepository.save(existingWish);
        } else {
            WishList wish = WishList.builder()
                .userId(userId)
                .optionId(optionId)
                .quantity(requestDTO.quantity())
                .build();
            wishRepository.save(wish);
        }
    }

    public List<WishListResponseDTO> getWishList(Long userId) {
        return wishRepository.findByUserId(userId).stream()
            .map(this::toWishListResponseDTO)
            .collect(Collectors.toList());
    }

    private WishListResponseDTO toWishListResponseDTO(WishList wish) {
        ResponseEntity<OptionDetailDTO> response = productServiceClient.getOptionDetails(
            wish.getOptionId());
        OptionDetailDTO optionDetails = response.getBody();

        return WishListResponseDTO.builder()
            .id(wish.getId())
            .quantity(wish.getQuantity())
            .optionId(optionDetails.getOptionId())
            .optionType(optionDetails.getOptionType())
            .availability(optionDetails.isAvailability())
            .stock(optionDetails.getStock())
            .productId(optionDetails.getProductId())
            .productName(optionDetails.getProductName())
            .build();
    }

    public void removeFromWishList(Long userId, Long wishId) {
        WishList wish = wishRepository.findByIdAndUserId(wishId, userId)
            .orElseThrow(() -> new WishNotFoundException());

        wishRepository.delete(wish);
    }

    public void updateWishQuantity(Long userId, Long wishId, AddWishListRequestDTO requestDTO) {
        WishList wish = wishRepository.findByIdAndUserId(wishId, userId)
            .orElseThrow(() -> new WishNotFoundException());

        ResponseEntity<Integer> stockResponse = productServiceClient.getOptionStock(
            wish.getOptionId());
        int availableStock = stockResponse.getBody();
        if (availableStock < requestDTO.quantity()) {
            throw new InsufficientStockException();
        }

        wish.setQuantity(requestDTO.quantity());
        wishRepository.save(wish);
    }
}
