package com.warren.clob.repos;

import com.warren.clob.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByBuyerIdOrSellerId(Long buyerId, Long sellerId);
}
