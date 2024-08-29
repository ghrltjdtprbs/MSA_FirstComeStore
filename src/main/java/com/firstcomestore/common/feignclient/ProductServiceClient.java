package com.firstcomestore.common.feignclient;

import com.firstcomestore.domain.wishlist.dto.response.OptionDetailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/product-service/options/{optionId}/exists")
    ResponseEntity<Boolean> checkOptionExists(@PathVariable("optionId") Long optionId);

    @GetMapping("/product-service/options/{optionId}/inventory")
    ResponseEntity<Integer> getOptionStock(@PathVariable("optionId") Long optionId);

    @GetMapping("/product-service/options/{optionId}/details")
    ResponseEntity<OptionDetailDTO> getOptionDetails(@PathVariable("optionId") Long optionId);
}
