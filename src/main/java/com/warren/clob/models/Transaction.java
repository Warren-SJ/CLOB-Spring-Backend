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
    private Long id;
    @Column(name="buyer")
    private Long buyerId;
    @Column(name="seller")
    private Long sellerId;
    @Column(name="price")
    private Integer price;
    @Column(name="quantity")
    private Integer quantity;
    @Column(name="ticker")
    private Long ticker;
    @Column(name="timestamp")
    private LocalDateTime timestamp;
}
