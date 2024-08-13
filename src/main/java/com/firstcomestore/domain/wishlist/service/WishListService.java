package com.firstcomestore.domain.wishlist.service;

import com.firstcomestore.domain.product.entity.Option;
import com.firstcomestore.domain.product.exception.ProductNotFoundException;
import com.firstcomestore.domain.product.repository.OptionRepository;
import com.firstcomestore.domain.user.entity.User;
import com.firstcomestore.domain.user.exception.UserNotFoundException;
import com.firstcomestore.domain.user.repository.UserRepository;
import com.firstcomestore.domain.wishlist.dto.request.AddWishListRequestDTO;
import com.firstcomestore.domain.wishlist.dto.response.WishListResponseDTO;
import com.firstcomestore.domain.wishlist.entity.WishList;
import com.firstcomestore.domain.wishlist.exception.WishNotFoundException;
import com.firstcomestore.domain.wishlist.repository.WishListRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WishListService {

    private final WishListRepository wishRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;

    public void addToWishList(Long userId, Long optionId, AddWishListRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException());

        Option option = optionRepository.findById(optionId)
            .orElseThrow(() -> new ProductNotFoundException());

        WishList existingWish = wishRepository.findByUserIdAndOptionId(userId, optionId);
        if (existingWish != null) {
            existingWish.setQuantity(existingWish.getQuantity() + requestDTO.quantity());
            wishRepository.save(existingWish);
        } else {
            WishList wish = WishList.builder()
                .user(user)
                .option(option)
                .quantity(requestDTO.quantity())
                .build();
            wishRepository.save(wish);
        }
    }

    public List<WishListResponseDTO> getWishList(Long userId) {
        return wishRepository.findByUserId(userId).stream()
            .map(wish -> WishListResponseDTO.builder()
                .id(wish.getId())
                .quantity(wish.getQuantity())
                .optionId(wish.getOption().getId())
                .optionType(wish.getOption().getType())
                .availability(wish.getOption().isAvailability())
                .stock(wish.getOption().getInventory().getStock())
                .productId(wish.getOption().getProduct().getId())
                .productName(wish.getOption().getProduct().getName())
                .build())
            .collect(Collectors.toList());
    }

    public void removeFromWishList(Long userId, Long wishId) {
        WishList wish = wishRepository.findByIdAndUserId(wishId, userId)
            .orElseThrow(() -> new WishNotFoundException());

        wishRepository.delete(wish);
    }

    public void updateWishQuantity(Long userId, Long wishId, AddWishListRequestDTO requestDTO) {
        WishList wish = wishRepository.findByIdAndUserId(wishId, userId)
            .orElseThrow(() -> new WishNotFoundException());

        wish.setQuantity(requestDTO.quantity());
        wishRepository.save(wish);
    }
}
