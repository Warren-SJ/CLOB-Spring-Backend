package com.warren.clob.services;

import com.warren.clob.models.Transaction;
import com.warren.clob.repos.TransactionRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TransactionService {

    final private TransactionRepo transactionRepo;

    List<Transaction> findAllByBuyerIdOrSellerId(Long buyerId, Long sellerId) {
        return transactionRepo.findAllByBuyerIdOrSellerId(buyerId, sellerId);
    }
}
