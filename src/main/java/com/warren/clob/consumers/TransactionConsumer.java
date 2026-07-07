package com.warren.clob.consumers;

import com.warren.clob.dto.DeductCash;
import com.warren.clob.services.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransactionConsumer {

    private final TransactionService transactionService;

    @RabbitListener(queues = "trade.results")
    public void receiveTransaction(DeductCash deductCash) {
        transactionService.executeTransaction(deductCash);
    }
}
