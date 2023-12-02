package com.menes.dw.service.impl;

import com.menes.dw.payload.request.CurrencyExchangeRequest;
import com.menes.dw.payload.response.CurrencyExchangeResponse;
import com.menes.dw.repository.CurrencyExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CurrencyExchangeServiceImpl implements com.menes.dw.service.CurrencyExchangeService {
    private final CurrencyExchangeRepository repository;

    @Override
    public List<CurrencyExchangeResponse> getAllCurrencyExchangeRateByDate(CurrencyExchangeRequest request) {
        var date = request.getDate();
        return repository
                .findAllByPublishDate(date)
                .stream()
                .map(row -> {
                    var response = new CurrencyExchangeResponse();
                    BeanUtils.copyProperties(row, response);
                    return response;
                })
                .collect(Collectors.toList());
    }

}
