package dto;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CurrencyDto {

    private String code;
    private String fullName;
    private String sign;

    @Override
    public String toString() {
        return "CurrencyDto{" +
                "code='" + code + '\'' +
                ", fullName='" + fullName + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyDto that = (CurrencyDto) o;
        return Objects.equals(code, that.code) && Objects.equals(fullName, that.fullName) && Objects.equals(sign, that.sign);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(code);
        result = 31 * result + Objects.hashCode(fullName);
        result = 31 * result + Objects.hashCode(sign);
        return result;
    }
}
