package dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CurrencyDto {

    private String code;
    private String fullName;
    private String sign;
}
