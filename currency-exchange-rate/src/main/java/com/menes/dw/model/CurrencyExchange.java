package com.menes.dw.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "currency_exchanges")
public class CurrencyExchange {
    @Id
    private Integer id;
}
