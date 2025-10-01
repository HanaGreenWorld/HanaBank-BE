package com.kopo.hanabank.investment.repository;

import com.kopo.hanabank.investment.domain.InvestmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentProductRepository extends JpaRepository<InvestmentProduct, Long> {
    
    List<InvestmentProduct> findByIsActiveTrue();
    
    List<InvestmentProduct> findByIsActiveTrueAndProductType(InvestmentProduct.ProductType productType);
    
    List<InvestmentProduct> findByIsActiveTrueAndRiskLevel(Integer riskLevel);
    
    List<InvestmentProduct> findByIsActiveTrueAndRiskLevelLessThanEqual(Integer maxRiskLevel);
}












