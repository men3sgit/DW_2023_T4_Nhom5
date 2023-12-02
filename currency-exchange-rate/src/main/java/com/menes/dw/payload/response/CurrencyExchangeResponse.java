package com.menes.dw.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.menes.dw.utils.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CurrencyExchangeResponse {
    private Long id;
    private String currencyCode;
    private String currencyName;
    private Double cashBuying;
    private Double telegraphicBuying;
    private Double selling;
    private String bankName;
    @JsonFormat(pattern = Pattern.DATE_FULL_DASH)
    private LocalDate publishDate;
    @JsonFormat(pattern = Pattern.TIME_HOUR_MINUTE)
    private LocalDateTime publishTime;
}
