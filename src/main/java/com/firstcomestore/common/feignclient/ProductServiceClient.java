package com.firstcomestore.common.feignclient;

import com.firstcomestore.common.dto.ResponseDTO;
import com.firstcomestore.domain.wishlist.dto.response.OptionDetailDTO;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    Logger logger = LoggerFactory.getLogger(ProductServiceClient.class);

    @CircuitBreaker(name = "productServiceClient", fallbackMethod = "checkOptionExistsFallback")
    @GetMapping("/product-service/options/{optionId}/exists")
    ResponseEntity<ResponseDTO<Boolean>> checkOptionExists(@PathVariable("optionId") Long optionId);

    @CircuitBreaker(name = "productServiceClient", fallbackMethod = "getOptionStockFallback")
    @GetMapping("/product-service/options/{optionId}/inventory")
    ResponseEntity<ResponseDTO<Integer>> getOptionStock(@PathVariable("optionId") Long optionId);

    @CircuitBreaker(name = "productServiceClient", fallbackMethod = "getOptionDetailsFallback")
    @GetMapping("/product-service/options/{optionId}/details")
    ResponseEntity<ResponseDTO<OptionDetailDTO>> getOptionDetails(
        @PathVariable("optionId") Long optionId);

    @CircuitBreaker(name = "productServiceClient", fallbackMethod = "getMaxPurchaseLimitFallback")
    @GetMapping("/product-service/options/{optionId}/max-purchase-limit")
    ResponseEntity<ResponseDTO<Integer>> getMaxPurchaseLimit(
        @PathVariable("optionId") Long optionId);

    @CircuitBreaker(name = "productServiceClient", fallbackMethod = "updateOptionStockFallback")
    @PutMapping("/product-service/options/{optionId}/inventory")
    ResponseEntity<ResponseDTO<Void>> updateOptionStock(@PathVariable("optionId") Long optionId,
        @RequestBody int stock);

    // Fallback Methods
    default ResponseEntity<ResponseDTO<Boolean>> checkOptionExistsFallback(Long optionId,
        Throwable throwable) {
        logError(throwable);
        return ResponseEntity.ok(ResponseDTO.okWithData(false));
    }

    default ResponseEntity<ResponseDTO<Integer>> getOptionStockFallback(Long optionId,
        Throwable throwable) {
        logError(throwable);
        return ResponseEntity.ok(ResponseDTO.okWithData(0));
    }

    default ResponseEntity<ResponseDTO<OptionDetailDTO>> getOptionDetailsFallback(Long optionId,
        Throwable throwable) {
        logError(throwable);
        OptionDetailDTO fallbackOption = OptionDetailDTO.builder()
            .optionId(optionId)
            .optionType("N/A")
            .availability(false)
            .stock(0)
            .productId(-1L)
            .productName("Unknown Product")
            .build();
        return ResponseEntity.ok(ResponseDTO.okWithData(fallbackOption));
    }

    default ResponseEntity<ResponseDTO<Integer>> getMaxPurchaseLimitFallback(Long optionId,
        Throwable throwable) {
        logError(throwable);
        return ResponseEntity.ok(ResponseDTO.okWithData(1));
    }

    default ResponseEntity<ResponseDTO<Void>> updateOptionStockFallback(Long optionId, int stock,
        Throwable throwable) {
        logError(throwable);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    private void logError(Throwable throwable) {
        if (throwable instanceof FeignException) {
            logger.error("product-service에 문제 발생! fallback method 호출 : {}", throwable.getMessage());
        } else {
            logger.error("product-service 에러! {}", throwable.getMessage());
        }
    }
}
