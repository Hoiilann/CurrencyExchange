package dto;

import entities.Currency;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRateWithAmountDto {

    private Currency baseCurrency;
    private Currency targetCurrency;
    private Float rate;
    private Float amount;
    private Float convertedAmount;

    @Override
    public String toString() {
        return "ExchangeRateWithAmountDto{" +
                "amount=" + amount +
                ", baseCurrency=" + baseCurrency +
                ", targetCurrency=" + targetCurrency +
                ", rate=" + rate +
                ", convertedAmount=" + convertedAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExchangeRateWithAmountDto that = (ExchangeRateWithAmountDto) o;
        return Objects.equals(baseCurrency, that.baseCurrency) && Objects.equals(targetCurrency, that.targetCurrency) && Objects.equals(rate, that.rate) && Objects.equals(amount, that.amount) && Objects.equals(convertedAmount, that.convertedAmount);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(baseCurrency);
        result = 31 * result + Objects.hashCode(targetCurrency);
        result = 31 * result + Objects.hashCode(rate);
        result = 31 * result + Objects.hashCode(amount);
        result = 31 * result + Objects.hashCode(convertedAmount);
        return result;
    }
}
