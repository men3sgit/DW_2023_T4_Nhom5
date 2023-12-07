package com.menes.dw.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.menes.dw.utils.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class CurrencyExchangeRequest {
    @JsonFormat(pattern = Pattern.DATE_FULL_DASH)
    private LocalDate date;
}
