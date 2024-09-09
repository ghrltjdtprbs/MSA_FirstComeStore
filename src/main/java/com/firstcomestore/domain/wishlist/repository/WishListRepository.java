package com.firstcomestore.domain.wishlist.repository;

import com.firstcomestore.domain.wishlist.entity.WishList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {

    List<WishList> findByUserId(Long userId);

    WishList findByUserIdAndOptionId(Long userId, Long optionId);

    Optional<WishList> findByIdAndUserId(Long id, Long userId);
}
