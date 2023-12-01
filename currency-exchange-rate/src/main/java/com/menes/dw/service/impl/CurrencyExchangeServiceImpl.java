package com.menes.dw.service.impl;

import com.menes.dw.model.CurrencyExchange;
import com.menes.dw.payload.request.CurrencyExchangeRequest;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CurrencyExchangeServiceImpl implements com.menes.dw.service.CurrencyExchangeService {
    @Override
    public List<CurrencyExchange> getAllCurrencyExchangeByDate(CurrencyExchangeRequest request) {
        return null;
    }
}
