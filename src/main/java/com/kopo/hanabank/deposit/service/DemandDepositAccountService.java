package com.kopo.hanabank.deposit.service;

import com.kopo.hanabank.common.exception.BusinessException;
import com.kopo.hanabank.common.exception.ErrorCode;
import com.kopo.hanabank.deposit.domain.DemandDepositAccount;
import com.kopo.hanabank.deposit.dto.DemandDepositAccountCreateRequest;
import com.kopo.hanabank.deposit.dto.DemandDepositAccountResponse;
import com.kopo.hanabank.deposit.repository.DemandDepositAccountRepository;
import com.kopo.hanabank.user.domain.User;
import com.kopo.hanabank.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DemandDepositAccountService {

    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final UserService userService;

    /**
     * 사용자 입출금 계좌 목록 조회
     */
    public List<DemandDepositAccountResponse> getUserAccounts(Long userId) {
        User user = userService.getUserById(userId);
        List<DemandDepositAccount> accounts = demandDepositAccountRepository.findByUser(user);

        return accounts.stream()
                .map(DemandDepositAccountResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 활성 입출금 계좌 목록 조회
     */
    public List<DemandDepositAccountResponse> getActiveUserAccounts(Long userId) {
        User user = userService.getUserById(userId);
        List<DemandDepositAccount> accounts = demandDepositAccountRepository.findActiveAccountsByUser(user);

        return accounts.stream()
                .map(DemandDepositAccountResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 계좌번호로 계좌 조회
     */
    public DemandDepositAccountResponse getAccountByNumber(String accountNumber) {
        DemandDepositAccount account = demandDepositAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEMAND_DEPOSIT_ACCOUNT_NOT_FOUND));

        return DemandDepositAccountResponse.from(account);
    }

    /**
     * 입출금 계좌 생성
     */
    @Transactional
    public DemandDepositAccountResponse createAccount(DemandDepositAccountCreateRequest request) {
        User user = userService.getUserById(request.getUserId());

        // 계좌번호 생성
        String accountNumber = generateAccountNumber();

        // 중복 체크
        if (demandDepositAccountRepository.existsByAccountNumber(accountNumber)) {
            throw new BusinessException(ErrorCode.DEMAND_DEPOSIT_ACCOUNT_ALREADY_EXISTS);
        }

        DemandDepositAccount account = DemandDepositAccount.builder()
                .user(user)
                .accountNumber(accountNumber)
                .accountName(request.getAccountName())
                .accountType(request.getAccountType())
                .openDate(LocalDate.now())
                .maturityDate(request.getMaturityDate())
                .baseInterestRate(request.getBaseInterestRate())
                .build();

        account = demandDepositAccountRepository.save(account);

        log.info("입출금 계좌 생성 완료 - 계좌번호: {}, 사용자: {}", accountNumber, user.getName());

        return DemandDepositAccountResponse.from(account);
    }

    /**
     * 입금
     */
    @Transactional
    public DemandDepositAccountResponse deposit(String accountNumber, Long amount) {
        DemandDepositAccount account = demandDepositAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEMAND_DEPOSIT_ACCOUNT_NOT_FOUND));

        if (!account.getIsActive() || account.getStatus() != DemandDepositAccount.AccountStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.INVALID_DEMAND_DEPOSIT_AMOUNT);
        }

        account.deposit(amount);
        demandDepositAccountRepository.save(account);

        log.info("입금 완료 - 계좌번호: {}, 금액: {}", accountNumber, amount);

        return DemandDepositAccountResponse.from(account);
    }

    /**
     * 출금
     */
    @Transactional
    public DemandDepositAccountResponse withdraw(String accountNumber, Long amount) {
        DemandDepositAccount account = demandDepositAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEMAND_DEPOSIT_ACCOUNT_NOT_FOUND));

        if (!account.getIsActive() || account.getStatus() != DemandDepositAccount.AccountStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.INVALID_DEMAND_DEPOSIT_AMOUNT);
        }

        try {
            account.withdraw(amount);
            demandDepositAccountRepository.save(account);

            log.info("출금 완료 - 계좌번호: {}, 금액: {}", accountNumber, amount);

            return DemandDepositAccountResponse.from(account);
        } catch (IllegalArgumentException e) {
            log.error("출금 실패 - 잔액 부족 - 계좌번호: {}, 요청금액: {}, 현재잔액: {}",
                accountNumber, amount, account.getAvailableBalance());
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }
    }

    /**
     * 계좌 해지
     */
    @Transactional
    public void closeAccount(String accountNumber) {
        DemandDepositAccount account = demandDepositAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEMAND_DEPOSIT_ACCOUNT_NOT_FOUND));

        account.close();
        demandDepositAccountRepository.save(account);

        log.info("계좌 해지 완료 - 계좌번호: {}", accountNumber);
    }

    /**
     * 계좌번호 생성
     */
    private String generateAccountNumber() {
        return "081" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
