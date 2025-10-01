package com.kopo.hanabank.savings.repository;

import com.kopo.hanabank.savings.domain.SavingsProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingsProductRepository extends JpaRepository<SavingsProduct, Long> {
    
    List<SavingsProduct> findByIsActiveTrue();
    
    List<SavingsProduct> findByProductTypeAndIsActiveTrue(String productType);
    
    List<SavingsProduct> findByTermMonthsAndIsActiveTrue(Integer termMonths);
    
    @Query("SELECT sp FROM SavingsProduct sp WHERE sp.isActive = true AND " +
           "(sp.productName LIKE %:keyword% OR sp.description LIKE %:keyword%)")
    List<SavingsProduct> findByKeywordAndIsActiveTrue(@Param("keyword") String keyword);
    
    List<SavingsProduct> findByIsActiveTrueAndDepositType(SavingsProduct.DepositType depositType);
}

