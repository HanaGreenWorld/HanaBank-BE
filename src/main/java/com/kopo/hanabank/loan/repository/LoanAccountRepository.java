package com.kopo.hanabank.loan.repository;

import com.kopo.hanabank.loan.domain.LoanAccount;
import com.kopo.hanabank.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanAccountRepository extends JpaRepository<LoanAccount, Long> {
    
    List<LoanAccount> findByUser(User user);
    
    List<LoanAccount> findByUserAndStatus(User user, LoanAccount.LoanStatus status);
    
    Optional<LoanAccount> findByAccountNumber(String accountNumber);
    
    boolean existsByAccountNumber(String accountNumber);
}




