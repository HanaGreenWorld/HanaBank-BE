package com.kopo.hanabank.loan.repository;

import com.kopo.hanabank.loan.domain.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
    
    List<LoanProduct> findByIsActiveTrue();
    
    List<LoanProduct> findByProductTypeAndIsActiveTrue(String productType);
    
    @Query("SELECT lp FROM LoanProduct lp WHERE lp.isActive = true AND " +
           "(lp.productName LIKE %:keyword% OR lp.description LIKE %:keyword%)")
    List<LoanProduct> findByKeywordAndIsActiveTrue(@Param("keyword") String keyword);
    
    List<LoanProduct> findByIsActiveTrueAndLoanType(LoanProduct.LoanType loanType);
    
    List<LoanProduct> findByIsActiveTrueAndMinAmountLessThanEqualAndMaxAmountGreaterThanEqual(Long amount, Long amount2);
}

