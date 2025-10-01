package com.kopo.hanabank.investment.repository;

import com.kopo.hanabank.investment.domain.InvestmentAccount;
import com.kopo.hanabank.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentAccountRepository extends JpaRepository<InvestmentAccount, Long> {
    
    List<InvestmentAccount> findByUser(User user);
    
    List<InvestmentAccount> findByUserAndStatus(User user, InvestmentAccount.AccountStatus status);
    
    Optional<InvestmentAccount> findByAccountNumber(String accountNumber);
    
    boolean existsByAccountNumber(String accountNumber);
    
}




