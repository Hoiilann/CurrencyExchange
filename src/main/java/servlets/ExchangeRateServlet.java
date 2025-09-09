package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRatesDao;
import dto.ExchangeRateDto;
import dto.ExchangeRatesDto;
import entities.ExchangeRate;
import lombok.NoArgsConstructor;
import services.ExchangeRateService;
import utils.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@NoArgsConstructor
@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();
    private ExchangeRatesDao exchangeRateDao = new ExchangeRatesDao();
    private ExchangeRateService exchangeRateService = new ExchangeRateService(new ExchangeRatesDao());

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(request.getMethod())) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String baseCurrencyCode = pathInfo.substring(1,4);
            String targetCurrencyCode = pathInfo.substring(4,7);

            ExchangeRate exchangeRate = exchangeRateService.getExchangeRateByCodes(baseCurrencyCode,targetCurrencyCode);
            if (exchangeRate != null) {
                String json = objectMapper.writeValueAsString(exchangeRate);
                response.getWriter().write(json);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,"Обменный курс для пары не найден");
                }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Коды валют пары отсутствуют в адресе");
        }
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        String jsonString = Util.getJsonString(request.getReader());

        ExchangeRatesDto exchangeRatesDto = objectMapper.readValue(jsonString, ExchangeRatesDto.class);

        if (pathInfo != null && pathInfo.length() > 1 && exchangeRatesDto.getRate() != null) {
            String baseCurrencyCode = pathInfo.substring(1, 4);
            String targetCurrencyCode = pathInfo.substring(4, 7);

            if (exchangeRateDao.updateExchangeRate(ExchangeRateDto.builder()
                                                              .baseCurrencyCode(baseCurrencyCode)
                                                              .targetCurrencyCode(targetCurrencyCode)
                                                              .rate(exchangeRatesDto.getRate())
                                                              .build()) == 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,"Валютная пара отсутствует в базе данных");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
        }

    }

}

