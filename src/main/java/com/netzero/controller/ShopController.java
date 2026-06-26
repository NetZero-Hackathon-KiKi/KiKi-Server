package com.netzero.controller;

import com.netzero.config.AuthUtil;
import com.netzero.dto.response.ApiResponse;
import com.netzero.dto.response.CharacterResponse;
import com.netzero.dto.response.ShopItemResponse;
import com.netzero.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "상점", description = "상점 아이템 조회/구매, 캐릭터 꾸미기 API")
@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @Operation(summary = "상점 아이템 목록", description = "구매 가능한 아이템 목록을 반환합니다. 카테고리 필터 가능.")
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ShopItemResponse>>> getShopItems(
            @Parameter(description = "카테고리 (HAT, CLOTHES, ACCESSORY, BACKGROUND, ETC)") @RequestParam(required = false) String category) {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getShopItems(AuthUtil.getCurrentUserId(), category)));
    }

    @Operation(summary = "아이템 구매", description = "GP를 사용하여 아이템을 구매합니다.")
    @PostMapping("/items/{itemId}/purchase")
    public ResponseEntity<ApiResponse<Map<String, Object>>> purchaseItem(
            @Parameter(description = "아이템 ID") @PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.ok("아이템을 구매했습니다.",
                shopService.purchaseItem(AuthUtil.getCurrentUserId(), itemId)));
    }

    @Operation(summary = "캐릭터 정보 조회", description = "레벨, XP, GP, 장착 아이템 목록을 반환합니다.")
    @GetMapping("/character")
    public ResponseEntity<ApiResponse<CharacterResponse>> getCharacterInfo() {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getCharacterInfo(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "아이템 장착", description = "보유 아이템을 캐릭터에 장착합니다.")
    @PutMapping("/character/equip/{itemId}")
    public ResponseEntity<ApiResponse<Void>> equipItem(
            @Parameter(description = "아이템 ID") @PathVariable Long itemId) {
        shopService.equipItem(AuthUtil.getCurrentUserId(), itemId);
        return ResponseEntity.ok(ApiResponse.ok("아이템을 장착했습니다.", null));
    }

    @Operation(summary = "아이템 해제", description = "장착 중인 아이템을 해제합니다.")
    @PutMapping("/character/unequip/{itemId}")
    public ResponseEntity<ApiResponse<Void>> unequipItem(
            @Parameter(description = "아이템 ID") @PathVariable Long itemId) {
        shopService.unequipItem(AuthUtil.getCurrentUserId(), itemId);
        return ResponseEntity.ok(ApiResponse.ok("아이템을 해제했습니다.", null));
    }

    @Operation(summary = "보유 아이템 목록", description = "구매한 아이템 목록을 반환합니다.")
    @GetMapping("/my-items")
    public ResponseEntity<ApiResponse<List<ShopItemResponse>>> getMyItems(
            @Parameter(description = "카테고리 필터") @RequestParam(required = false) String category) {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getMyItems(AuthUtil.getCurrentUserId(), category)));
    }
}
