package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Util {

    public static String getJsonString (BufferedReader reader) throws IOException {
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        return jsonBuilder.toString();

    }

    public static Float multiplyFloat (Float float1, Float float2) {
        BigDecimal number1 = new BigDecimal(Float.toString(float1));
        BigDecimal number2 = new BigDecimal(Float.toString(float2));

        return (number1.multiply(number2))
                .setScale(4, RoundingMode.HALF_UP)
                .floatValue();
    }

    public static Float divideFloat (Float dividend, Float divisor) {
        BigDecimal numberDividend = new BigDecimal(Float.toString(dividend));
        BigDecimal numberDivisor = new BigDecimal(Float.toString(divisor));

        return (numberDividend.divide(numberDivisor, 4, RoundingMode.HALF_UP))
                .floatValue();
    }
}
