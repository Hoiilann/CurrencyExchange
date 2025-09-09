package dto;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeDto {

    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private Float amount;

    @Override
    public String toString() {
        return "ExchangeDto{" +
                "amount=" + amount +
                ", baseCurrencyCode='" + baseCurrencyCode + '\'' +
                ", targetCurrencyCode='" + targetCurrencyCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExchangeDto that = (ExchangeDto) o;
        return Objects.equals(baseCurrencyCode, that.baseCurrencyCode) && Objects.equals(targetCurrencyCode, that.targetCurrencyCode) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(baseCurrencyCode);
        result = 31 * result + Objects.hashCode(targetCurrencyCode);
        result = 31 * result + Objects.hashCode(amount);
        return result;
    }
}
