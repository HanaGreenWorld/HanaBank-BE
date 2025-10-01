package com.kopo.hanabank.savings.service;

import com.kopo.hanabank.savings.domain.SavingsProduct;
import com.kopo.hanabank.savings.dto.SavingsProductResponse;
import com.kopo.hanabank.savings.repository.SavingsProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SavingsProductService {

    private final SavingsProductRepository savingsProductRepository;

    public List<SavingsProductResponse> getAllActiveSavingsProducts() {
        List<SavingsProduct> products = savingsProductRepository.findByIsActiveTrue();
        return products.stream()
                .map(SavingsProductResponse::new)
                .collect(Collectors.toList());
    }

    public List<SavingsProductResponse> getSavingsProductsByType(String productType) {
        List<SavingsProduct> products = savingsProductRepository.findByProductTypeAndIsActiveTrue(productType);
        return products.stream()
                .map(SavingsProductResponse::new)
                .collect(Collectors.toList());
    }

    public SavingsProductResponse getSavingsProductById(Long productId) {
        SavingsProduct product = savingsProductRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("적금 상품을 찾을 수 없습니다."));
        return new SavingsProductResponse(product);
    }

    public List<SavingsProductResponse> searchSavingsProducts(String keyword) {
        List<SavingsProduct> products = savingsProductRepository.findByKeywordAndIsActiveTrue(keyword);
        return products.stream()
                .map(SavingsProductResponse::new)
                .collect(Collectors.toList());
    }
}
