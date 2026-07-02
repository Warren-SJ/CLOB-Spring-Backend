package com.warren.clob.services;

import com.warren.clob.dto.DeductCash;
import com.warren.clob.models.Transaction;
import com.warren.clob.models.User;
import com.warren.clob.repos.TransactionRepo;
import com.warren.clob.repos.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TransactionService {

    final private TransactionRepo transactionRepo;
    final private UserRepo userRepo;

    public List<Transaction> getTransactionsByClientId(long clientId, long clientId1) {
        return transactionRepo.findAllByBuyerIdOrSellerId(clientId, clientId1);
    }

    @Transactional
    public void executeTransaction(DeductCash deductCash) {
        Integer quantity = deductCash.getQuantity();
        Integer price = deductCash.getPrice();
        Long buyerId = deductCash.getBuyerId();
        Long sellerId = deductCash.getSellerId();
        
        User buyer = userRepo.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found with ID: " + buyerId));
        User seller = userRepo.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found with ID: " + sellerId));

        int totalCost = price * quantity;
        buyer.setCash(buyer.getCash() - totalCost);
        seller.setCash(seller.getCash() + totalCost);
        seller.setBuyingPower(seller.getBuyingPower() + totalCost);

        userRepo.save(buyer);
        userRepo.save(seller);
    }
}
