package com.firstcomestore.domain.wishlist.repository;

import com.firstcomestore.domain.wishlist.entity.WishList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
    // 특정 사용자의 위시리스트를 모두 조회
    List<WishList> findByUserId(Long userId);
    WishList findByUserIdAndOptionId(Long userId, Long optionId);

    // 특정 사용자의 특정 위시리스트 항목 조회
    Optional<WishList> findByIdAndUserId(Long id, Long userId);

}
