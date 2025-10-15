package com.kopo.hanabank.electronicreceipt.repository;

import com.kopo.hanabank.electronicreceipt.domain.ElectronicReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ElectronicReceiptRepository extends JpaRepository<ElectronicReceipt, Long> {

    Optional<ElectronicReceipt> findByTransactionId(String transactionId);

    List<ElectronicReceipt> findByCustomerIdOrderByReceiptDateDesc(Long customerId);

    @Query("SELECT e FROM ElectronicReceipt e WHERE e.customerId = :customerId " +
           "AND e.receiptDate BETWEEN :startDate AND :endDate " +
           "ORDER BY e.receiptDate DESC")
    List<ElectronicReceipt> findByCustomerIdAndDateRange(
        @Param("customerId") Long customerId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT e FROM ElectronicReceipt e WHERE e.isGreenWorldUser = true " +
           "AND e.webhookSent = false " +
           "ORDER BY e.receiptDate ASC")
    List<ElectronicReceipt> findUnsentWebhookReceipts();

}
