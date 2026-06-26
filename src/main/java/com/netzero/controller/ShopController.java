package com.netzero.controller;

import com.netzero.dto.request.PurchaseRequest;
import com.netzero.dto.response.ApiResponse;
import com.netzero.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Shop", description = "상점 관련 API")
@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @Operation(summary = "상점 아이템 목록 조회", description = "구매 가능한 상점 아이템 목록을 조회합니다.")
    @GetMapping("/items")
    public ApiResponse<?> getItems(@Parameter(description = "유저 ID") @RequestParam Long userId) {
        return ApiResponse.ok(shopService.getAllItems());
    }

    @Operation(summary = "아이템 구매", description = "GP를 사용하여 아이템을 구매합니다.")
    @PostMapping("/purchase")
    public ApiResponse<?> purchase(@Parameter(description = "유저 ID") @RequestParam Long userId,
                                   @RequestBody PurchaseRequest request) {
        try {
            return ApiResponse.ok(shopService.purchase(userId, request.getItemId()));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @Operation(summary = "내 아이템 목록 조회", description = "해당 유저가 구매한 아이템 목록을 조회합니다.")
    @GetMapping("/my-items")
    public ApiResponse<?> getMyItems(@Parameter(description = "유저 ID") @RequestParam Long userId) {
        return ApiResponse.ok(shopService.getMyItems(userId));
    }
}
