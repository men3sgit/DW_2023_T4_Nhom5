package com.menes.dw.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRateInfo {
    private String currencyCode;
    private String currencyName;
    private double buying;
    private double teleBuying;
    private double selling;
    private String bankName;


}