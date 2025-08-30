package entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Currency {

    private Long id;
    private String code;
    private String fullName;
    private String sign;

    @Override
    public String toString() {
        return "Currency{" +
                "code='" + code + '\'' +
                ", id=" + id +
                ", fullName='" + fullName + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
