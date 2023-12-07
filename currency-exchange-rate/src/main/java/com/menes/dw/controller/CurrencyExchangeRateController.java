package com.menes.dw.controller;

import com.menes.dw.payload.request.CurrencyExchangeRequest;
import com.menes.dw.service.CurrencyExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("api/v1/currency-exchange-rates")
public class CurrencyExchangeRateController {
    private final CurrencyExchangeService service;

    @GetMapping(path = {"/today", "/", ""})
    public ResponseEntity<?> getAllCurrencyExchangeRate() {
        return getAllCurrencyExchangeRate(LocalDate.now());
    }

    @GetMapping("/{date}")
    public ResponseEntity<?> getAllCurrencyExchangeRate(
            @PathVariable("date")
            @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date) {

        var request = new CurrencyExchangeRequest();
        request.setDate(date);
        return ResponseEntity.ok(service.getAllCurrencyExchangeRateByDate(request));
    }

}
