package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRatesDao;
import dto.ExchangeRatesDto;
import entities.ExchangeRate;
import lombok.NoArgsConstructor;
import utils.Util;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@NoArgsConstructor
@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExchangeRatesDao exchangeRateDao = new ExchangeRatesDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<ExchangeRate> exchangeRates = exchangeRateDao.findAllExchangeRates();

        String json = objectMapper.writeValueAsString(exchangeRates);

        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String jsonString = Util.getJsonString(request.getReader());

        ExchangeRatesDto newExchangeRate = objectMapper.readValue(jsonString,ExchangeRatesDto.class);

        if (newExchangeRate.getBaseCurrencyCode() == null || newExchangeRate.getTargetCurrencyCode() == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Отсутствует нужное поле формы");
        } else if (newExchangeRate.getTargetCurrencyCode().equals(newExchangeRate.getBaseCurrencyCode())) {
            response.sendError(HttpServletResponse.SC_CONFLICT, "Обмен валют невозможен");
        } else {
                exchangeRateDao.addExchangeRate(newExchangeRate);
        }

    }


}

