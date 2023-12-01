package com.menes.dw.controller;

import com.menes.dw.service.CurrencyExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/currency-exchange-rate")
public class CurrencyExchangeController {
    private final CurrencyExchangeService service;

    @GetMapping
    public ResponseEntity<?> greeting() {
        return ResponseEntity.ok("Helle, Have a nice day!");
    }
}
