package com.adrian.bank.management.system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreatedDate
    private LocalDate openingDate;

    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public void subtractFromBalance(BigDecimal value) {
        this.balance = this.balance.subtract(value);
    }

    public void addToBalance(BigDecimal value) {
        this.balance = this.balance.add(value);
    }
}
