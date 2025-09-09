package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import entities.Currency;
import lombok.NoArgsConstructor;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@NoArgsConstructor
@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyDao currencyDao = new CurrencyDao();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String code = pathInfo.substring(1);

            Currency currency = currencyDao.getCurrencyByCode(code);


            if (currency == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,"Валюта не найдена");
            } else {
                String json = objectMapper.writeValueAsString(currency);

                response.getWriter().write(json);
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Код валюты отсутствует в адресе");
        }

    }

}
