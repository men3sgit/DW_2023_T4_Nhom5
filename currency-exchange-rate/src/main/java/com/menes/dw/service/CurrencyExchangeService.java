package com.menes.dw.service;

import com.menes.dw.payload.request.CurrencyExchangeRequest;
import com.menes.dw.payload.response.CurrencyExchangeResponse;

import java.util.List;

public interface CurrencyExchangeService {
    List<CurrencyExchangeResponse> getAllCurrencyExchangeRateByDate(CurrencyExchangeRequest request);
}
