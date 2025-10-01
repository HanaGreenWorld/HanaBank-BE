package com.kopo.hanabank.deposit.repository;

import com.kopo.hanabank.deposit.domain.DemandDepositAccount;
import com.kopo.hanabank.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandDepositAccountRepository extends JpaRepository<DemandDepositAccount, Long> {

    List<DemandDepositAccount> findByUser(User user);

    List<DemandDepositAccount> findByUserAndIsActiveTrue(User user);

    Optional<DemandDepositAccount> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT d FROM DemandDepositAccount d WHERE d.user = :user AND d.isActive = true AND d.status = 'ACTIVE'")
    List<DemandDepositAccount> findActiveAccountsByUser(User user);
}
