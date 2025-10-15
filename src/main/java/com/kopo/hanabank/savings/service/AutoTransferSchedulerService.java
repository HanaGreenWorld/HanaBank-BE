package com.kopo.hanabank.savings.service;

import com.kopo.hanabank.savings.domain.SavingsAccount;
import com.kopo.hanabank.savings.repository.SavingsAccountRepository;
import com.kopo.hanabank.deposit.domain.DemandDepositAccount;
import com.kopo.hanabank.deposit.repository.DemandDepositAccountRepository;
import com.kopo.hanabank.user.domain.User;
import com.kopo.hanabank.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoTransferSchedulerService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void processAutoTransfers() {
        LocalDate today = LocalDate.now();
        int todayDay = today.getDayOfMonth();

        // 오늘 자동이체해야 할 적금 계좌들 조회
        List<SavingsAccount> accountsToTransfer = savingsAccountRepository
                .findByAutoTransferEnabledTrueAndTransferDay(todayDay);

        for (SavingsAccount savingsAccount : accountsToTransfer) {
            try {
                processAutoTransfer(savingsAccount);
            } catch (Exception e) {
                log.error("자동이체 처리 실패 - 계좌번호: {}, 오류: {}", 
                    savingsAccount.getAccountNumber(), e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void processAutoTransfer(SavingsAccount savingsAccount) {

        // 계좌 상태 확인
        if (!savingsAccount.getIsActive() || 
            savingsAccount.getStatus() != SavingsAccount.AccountStatus.ACTIVE) {
            log.warn("적금 계좌가 비활성 상태입니다 - 계좌번호: {}", savingsAccount.getAccountNumber());
            return;
        }

        // 자동이체 금액 확인
        if (savingsAccount.getMonthlyTransferAmount() == null || 
            savingsAccount.getMonthlyTransferAmount() <= 0) {
            log.warn("자동이체 금액이 설정되지 않았습니다 - 계좌번호: {}", savingsAccount.getAccountNumber());
            return;
        }

        // 출금 계좌 조회
        String withdrawalAccountNumber = savingsAccount.getWithdrawalAccountNumber();
        if (withdrawalAccountNumber == null || withdrawalAccountNumber.trim().isEmpty()) {
            log.warn("출금 계좌번호가 설정되지 않았습니다 - 적금계좌: {}", savingsAccount.getAccountNumber());
            return;
        }

        DemandDepositAccount withdrawalAccount = demandDepositAccountRepository
                .findByAccountNumber(withdrawalAccountNumber)
                .orElse(null);

        if (withdrawalAccount == null) {
            log.warn("출금 계좌를 찾을 수 없습니다 - 출금계좌: {}", withdrawalAccountNumber);
            return;
        }

        // 출금 계좌 상태 확인
        if (!withdrawalAccount.getIsActive() || 
            withdrawalAccount.getStatus() != DemandDepositAccount.AccountStatus.ACTIVE) {
            log.warn("출금 계좌가 비활성 상태입니다 - 출금계좌: {}", withdrawalAccountNumber);
            return;
        }

        // 출금 계좌 잔액 확인
        Long transferAmount = savingsAccount.getMonthlyTransferAmount();
        if (withdrawalAccount.getBalance() < transferAmount) {
            log.warn("출금 계좌 잔액 부족 - 출금계좌: {}, 현재잔액: {}, 요청금액: {}", 
                withdrawalAccountNumber, withdrawalAccount.getBalance(), transferAmount);
            return;
        }

        try {
            // 출금 계좌에서 출금
            withdrawalAccount.withdraw(transferAmount);
            demandDepositAccountRepository.save(withdrawalAccount);
            
            // 적금 계좌에 입금
            savingsAccount.deposit(transferAmount);
            savingsAccountRepository.save(savingsAccount);
            
            log.info("자동이체 완료 - 출금계좌: {}, 적금계좌: {}, 이체금액: {}, 출금계좌잔액: {}, 적금계좌잔액: {}", 
                withdrawalAccountNumber, 
                savingsAccount.getAccountNumber(), 
                transferAmount,
                withdrawalAccount.getBalance(),
                savingsAccount.getBalance());
                
        } catch (Exception e) {
            log.error("자동이체 실행 중 오류 발생 - 출금계좌: {}, 적금계좌: {}, 오류: {}", 
                withdrawalAccountNumber, savingsAccount.getAccountNumber(), e.getMessage(), e);
            throw e; // 트랜잭션 롤백을 위해 예외 재발생
        }
    }

    @Transactional
    public void processAutoTransfersForDate(LocalDate date) {
        int day = date.getDayOfMonth();
        
        log.info("수동 자동이체 실행 - 날짜: {}, 날짜: {}", date, day);

        List<SavingsAccount> accountsToTransfer = savingsAccountRepository
                .findByAutoTransferEnabledTrueAndTransferDay(day);

        for (SavingsAccount savingsAccount : accountsToTransfer) {
            try {
                processAutoTransfer(savingsAccount);
            } catch (Exception e) {
                log.error("자동이체 처리 실패 - 계좌번호: {}, 오류: {}", 
                    savingsAccount.getAccountNumber(), e.getMessage(), e);
            }
        }
    }
}