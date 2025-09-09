package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;

import dto.CurrencyDto;
import entities.Currency;
import lombok.NoArgsConstructor;
import utils.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@NoArgsConstructor
@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyDao currencyDao = new CurrencyDao();



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        List<Currency> currencies = currencyDao.findAllCurrencies();

        String json = objectMapper.writeValueAsString(currencies);

        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String jsonString = Util.getJsonString(request.getReader());

        CurrencyDto newCurrency = objectMapper.readValue(jsonString, CurrencyDto.class);

        if (newCurrency.getCode() == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
        } else {
            currencyDao.addCurrency(newCurrency);
        }
    }
}
