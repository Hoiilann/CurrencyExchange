package dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeDto {

    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private Float amount;
}
