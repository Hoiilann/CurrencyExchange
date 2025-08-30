package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRatesDao;
import dto.ExchangeDto;
import dto.ExchangeRateWithAmountDto;
import lombok.NoArgsConstructor;
import services.ExchangeRateService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@NoArgsConstructor
@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();
    private ExchangeRateService exchangeRateService = new ExchangeRateService(new ExchangeRatesDao());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseCurrencyCode = request.getParameter("from");
        String targetCurrencyCode = request.getParameter("to");
        Float amount = Float.valueOf(request.getParameter("amount"));

        ExchangeRateWithAmountDto exchangeRateWithAmountDto =
                exchangeRateService.convertAmount(ExchangeDto.builder()
                                                             .baseCurrencyCode(baseCurrencyCode)
                                                             .targetCurrencyCode(targetCurrencyCode)
                                                             .amount(amount)
                                                             .build());

        String json = objectMapper.writeValueAsString(exchangeRateWithAmountDto);

        response.getWriter().write(json);
        if (exchangeRateWithAmountDto == null) {
            response.sendError(HttpServletResponse.SC_CONFLICT, "Расчёт перевода средств из одной валюты в другую невозможен");
        }



    }
}
