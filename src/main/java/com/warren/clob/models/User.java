package com.warren.clob.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="clients")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="name")
    private String name;
    @Column(name="contact")
    private String contact;
    @Column(name="email")
    private String email;
    @Column(name="address")
    private String address;
    @Column(name="cash")
    private Integer cash;
    @Column(name="buying_power")
    private Integer buyingPower;
    @Column(name="password")
    private String password;
}
