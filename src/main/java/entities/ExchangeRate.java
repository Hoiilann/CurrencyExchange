package entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRate {

    private Long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private Float rate;

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "baseCurrency=" + baseCurrency +
                ", id=" + id +
                ", targetCurrency=" + targetCurrency +
                ", rate=" + rate +
                '}';
    }
}
