package com.menes.dw.controller;

import com.menes.dw.model.ExchangeRateInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/currency-exchange-rates-temp")
public class ExchangeRateController {

    @GetMapping
    public List<ExchangeRateInfo> getExchangeRates() {
        // Simulated exchange rate data
        List<ExchangeRateInfo> exchangeRates = new ArrayList<>();
        exchangeRates.add(new ExchangeRateInfo("USD", "US Dollar", 1.0, 1.0, 1.0, "Bank A"));
        exchangeRates.add(new ExchangeRateInfo("EUR", "Euro", 0.85, 0.85, 0.85, "Bank B"));
        exchangeRates.add(new ExchangeRateInfo("GBP", "British Pound", 0.75, 0.75, 0.75, "Bank C"));

        return exchangeRates;
    }
}
