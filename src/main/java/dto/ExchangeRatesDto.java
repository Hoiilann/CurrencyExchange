package dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExchangeRatesDto {

    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private Float rate;
}
