package com.warren.clob.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="trades")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @Column(name="id")
    private long id;
    @Column(name="buyer")
    private long buyerId;
    @Column(name="seller")
    private long sellerId;
    @Column(name="price")
    private int price;
    @Column(name="quantity")
    private int quantity;
    @Column(name="ticker")
    private long ticker;
    @Column(name="timestamp")
    private LocalDateTime timestamp;
}
