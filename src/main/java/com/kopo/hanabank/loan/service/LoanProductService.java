package com.kopo.hanabank.loan.service;

import com.kopo.hanabank.loan.domain.LoanProduct;
import com.kopo.hanabank.loan.dto.LoanProductResponse;
import com.kopo.hanabank.loan.repository.LoanProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanProductService {

    private final LoanProductRepository loanProductRepository;

    public List<LoanProductResponse> getAllActiveLoanProducts() {
        List<LoanProduct> products = loanProductRepository.findByIsActiveTrue();
        return products.stream()
                .map(LoanProductResponse::new)
                .collect(Collectors.toList());
    }

    public List<LoanProductResponse> getLoanProductsByType(String productType) {
        List<LoanProduct> products = loanProductRepository.findByProductTypeAndIsActiveTrue(productType);
        return products.stream()
                .map(LoanProductResponse::new)
                .collect(Collectors.toList());
    }

    public LoanProductResponse getLoanProductById(Long productId) {
        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("대출 상품을 찾을 수 없습니다."));
        return new LoanProductResponse(product);
    }

    public List<LoanProductResponse> searchLoanProducts(String keyword) {
        List<LoanProduct> products = loanProductRepository.findByKeywordAndIsActiveTrue(keyword);
        return products.stream()
                .map(LoanProductResponse::new)
                .collect(Collectors.toList());
    }
}
