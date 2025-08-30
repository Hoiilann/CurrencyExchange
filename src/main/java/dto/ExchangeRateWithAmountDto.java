package dto;

import entities.Currency;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRateWithAmountDto {

    private Currency baseCurrency;
    private Currency targetCurrency;
    private Float rate;
    private Float amount;
    private Float convertedAmount;
}
