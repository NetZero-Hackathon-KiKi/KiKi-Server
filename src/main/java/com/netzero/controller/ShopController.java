package com.netzero.controller;

import com.netzero.dto.request.PurchaseRequest;
import com.netzero.dto.response.ApiResponse;
import com.netzero.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    // 상점 아이템 목록
    @GetMapping("/items")
    public ApiResponse<?> getItems() {
        return ApiResponse.ok(shopService.getAllItems());
    }

    // 아이템 구매 (GP 차감)
    @PostMapping("/purchase")
    public ApiResponse<?> purchase(@RequestParam Long userId, @RequestBody PurchaseRequest request) {
        try {
            return ApiResponse.ok(shopService.purchase(userId, request.getItemId()));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 내 아이템 목록
    @GetMapping("/my-items")
    public ApiResponse<?> getMyItems(@RequestParam Long userId) {
        return ApiResponse.ok(shopService.getMyItems(userId));
    }
}
