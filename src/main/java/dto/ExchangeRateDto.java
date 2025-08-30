package dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRateDto {

    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private Float rate;
}
