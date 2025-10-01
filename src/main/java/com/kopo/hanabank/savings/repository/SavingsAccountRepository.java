package com.kopo.hanabank.savings.repository;

import com.kopo.hanabank.savings.domain.SavingsAccount;
import com.kopo.hanabank.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    
    List<SavingsAccount> findByUser(User user);
    
    List<SavingsAccount> findByUserAndStatus(User user, SavingsAccount.AccountStatus status);
    
    Optional<SavingsAccount> findByAccountNumber(String accountNumber);
    
    boolean existsByAccountNumber(String accountNumber);
    
    // 자동이체 관련 쿼리 메서드들
    List<SavingsAccount> findByAutoTransferEnabledTrueAndTransferDay(Integer transferDay);
    
    List<SavingsAccount> findByUserAndAutoTransferEnabledTrue(User user);
    
}