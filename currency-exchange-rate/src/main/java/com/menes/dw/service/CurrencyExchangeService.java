package com.menes.dw.service;

import com.menes.dw.model.CurrencyExchange;
import com.menes.dw.payload.request.CurrencyExchangeRequest;

import java.time.LocalDate;
import java.util.List;

public interface CurrencyExchangeService {
    List<CurrencyExchange> getAllCurrencyExchangeByDate(CurrencyExchangeRequest request);
}
