package com.firstcomestore.domain.wishlist.service;

import com.firstcomestore.common.dto.ResponseDTO;
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
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishRepository;
    private final ProductServiceClient productServiceClient;

    @Override
    public void addToWishList(Long userId, Long optionId, AddWishListRequestDTO requestDTO) {
        ResponseEntity<ResponseDTO<Boolean>> response = productServiceClient.checkOptionExists(
            optionId);
        if (!response.getBody().getData()) {
            throw new OptionNotFoundException();
        }

        ResponseEntity<ResponseDTO<Integer>> stockResponse = productServiceClient.getOptionStock(
            optionId);
        int availableStock = stockResponse.getBody().getData();
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

    @Transactional(readOnly = true)
    @Override
    public List<WishListResponseDTO> getWishList(Long userId) {
        return wishRepository.findByUserId(userId).stream()
            .map(this::toWishListResponseDTO)
            .collect(Collectors.toList());
    }

    private WishListResponseDTO toWishListResponseDTO(WishList wish) {
        ResponseEntity<ResponseDTO<OptionDetailDTO>> response = productServiceClient.getOptionDetails(
            wish.getOptionId());
        OptionDetailDTO optionDetails = response.getBody().getData();

        return WishListResponseDTO.builder()
            .id(wish.getId())
            .quantity(wish.getQuantity())
            .optionId(optionDetails.optionId())
            .optionType(optionDetails.optionType())
            .availability(optionDetails.availability())
            .stock(optionDetails.stock())
            .productId(optionDetails.productId())
            .productName(optionDetails.productName())
            .build();
    }

    @Override
    public void removeFromWishList(Long userId, Long wishId) {
        WishList wish = wishRepository.findByIdAndUserId(wishId, userId)
            .orElseThrow(() -> new WishNotFoundException());

        wishRepository.delete(wish);
    }

    @Override
    public void updateWishQuantity(Long userId, Long wishId, AddWishListRequestDTO requestDTO) {
        WishList wish = wishRepository.findByIdAndUserId(wishId, userId)
            .orElseThrow(() -> new WishNotFoundException());

        ResponseEntity<ResponseDTO<Integer>> stockResponse = productServiceClient.getOptionStock(
            wish.getOptionId());
        int availableStock = stockResponse.getBody().getData();
        if (availableStock < requestDTO.quantity()) {
            throw new InsufficientStockException();
        }

        wish.setQuantity(requestDTO.quantity());
        wishRepository.save(wish);
    }
}
