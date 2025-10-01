package com.kopo.hanabank.savings.controller;

import com.kopo.hanabank.common.dto.ApiResponse;
import com.kopo.hanabank.savings.dto.SavingsProductResponse;
import com.kopo.hanabank.savings.service.SavingsProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "적금 상품 관리", description = "적금 상품 관련 API")
@RestController
@RequestMapping("/savings/products")
@RequiredArgsConstructor
public class SavingsProductController {

    private final SavingsProductService savingsProductService;

    @Operation(summary = "적금 상품 목록 조회", description = "모든 활성 적금 상품을 조회합니다.")
    @GetMapping("")
    public ApiResponse<List<SavingsProductResponse>> getAllSavingsProducts() {
        List<SavingsProductResponse> products = savingsProductService.getAllActiveSavingsProducts();
        return ApiResponse.success(products);
    }

    @Operation(summary = "적금 상품 타입별 조회", description = "적금 상품 타입별로 조회합니다.")
    @GetMapping("/type/{productType}")
    public ApiResponse<List<SavingsProductResponse>> getSavingsProductsByType(@PathVariable String productType) {
        List<SavingsProductResponse> products = savingsProductService.getSavingsProductsByType(productType);
        return ApiResponse.success(products);
    }

    @Operation(summary = "적금 상품 조회", description = "ID로 적금 상품을 조회합니다.")
    @GetMapping("/{productId}")
    public ApiResponse<SavingsProductResponse> getSavingsProduct(@PathVariable Long productId) {
        SavingsProductResponse product = savingsProductService.getSavingsProductById(productId);
        return ApiResponse.success(product);
    }

    @Operation(summary = "적금 상품 검색", description = "키워드로 적금 상품을 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<List<SavingsProductResponse>> searchSavingsProducts(@RequestParam String keyword) {
        List<SavingsProductResponse> products = savingsProductService.searchSavingsProducts(keyword);
        return ApiResponse.success(products);
    }
}
