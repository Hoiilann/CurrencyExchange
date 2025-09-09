package dto;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRateDto {

    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private Float rate;

    @Override
    public String toString() {
        return "ExchangeRateDto{" +
                "baseCurrencyCode='" + baseCurrencyCode + '\'' +
                ", targetCurrencyCode='" + targetCurrencyCode + '\'' +
                ", rate=" + rate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExchangeRateDto that = (ExchangeRateDto) o;
        return Objects.equals(baseCurrencyCode, that.baseCurrencyCode) && Objects.equals(targetCurrencyCode, that.targetCurrencyCode) && Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(baseCurrencyCode);
        result = 31 * result + Objects.hashCode(targetCurrencyCode);
        result = 31 * result + Objects.hashCode(rate);
        return result;
    }
}
