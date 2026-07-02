package com.warren.clob.controllers;

import com.warren.clob.dto.DeductCash;
import com.warren.clob.models.Transaction;
import com.warren.clob.services.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class TransactionController {
    final private TransactionService transactionService;

    @GetMapping("api/transaction/client/{clientId}")
    public List<Transaction> getTransactionsByClientId(@PathVariable long clientId) {
        return transactionService.getTransactionsByClientId(clientId, clientId);
    }

    @PostMapping("api/transaction/execute")
    public void executeTransaction(@RequestBody DeductCash deductCash) {
        transactionService.executeTransaction(deductCash);
    }
}
