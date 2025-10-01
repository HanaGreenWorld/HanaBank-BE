package com.kopo.hanabank.loan.controller;

import com.kopo.hanabank.common.dto.ApiResponse;
import com.kopo.hanabank.loan.dto.LoanProductResponse;
import com.kopo.hanabank.loan.service.LoanProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "대출 상품 관리", description = "대출 상품 관련 API")
@RestController
@RequestMapping("/loans/products")
@RequiredArgsConstructor
public class LoanProductController {

    private final LoanProductService loanProductService;

    @Operation(summary = "대출 상품 목록 조회", description = "모든 활성 대출 상품을 조회합니다.")
    @GetMapping("")
    public ApiResponse<List<LoanProductResponse>> getAllLoanProducts() {
        List<LoanProductResponse> products = loanProductService.getAllActiveLoanProducts();
        return ApiResponse.success(products);
    }

    @Operation(summary = "대출 상품 타입별 조회", description = "대출 상품 타입별로 조회합니다.")
    @GetMapping("/type/{productType}")
    public ApiResponse<List<LoanProductResponse>> getLoanProductsByType(@PathVariable String productType) {
        List<LoanProductResponse> products = loanProductService.getLoanProductsByType(productType);
        return ApiResponse.success(products);
    }

    @Operation(summary = "대출 상품 조회", description = "ID로 대출 상품을 조회합니다.")
    @GetMapping("/{productId}")
    public ApiResponse<LoanProductResponse> getLoanProduct(@PathVariable Long productId) {
        LoanProductResponse product = loanProductService.getLoanProductById(productId);
        return ApiResponse.success(product);
    }

    @Operation(summary = "대출 상품 검색", description = "키워드로 대출 상품을 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<List<LoanProductResponse>> searchLoanProducts(@RequestParam String keyword) {
        List<LoanProductResponse> products = loanProductService.searchLoanProducts(keyword);
        return ApiResponse.success(products);
    }
}
