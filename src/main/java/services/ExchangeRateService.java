package services;

import dao.ExchangeRatesDao;
import dto.ExchangeDto;
import dto.ExchangeRateWithAmountDto;
import entities.Currency;
import entities.ExchangeRate;
import lombok.RequiredArgsConstructor;
import utils.Util;

import java.util.List;

@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRatesDao exchangeRateDao;

    public ExchangeRateWithAmountDto convertAmount (ExchangeDto exchangeDto) {

        List<ExchangeRate> exchangeRates = exchangeRateDao.getExchangeRateByCodes(exchangeDto.getBaseCurrencyCode(),exchangeDto.getTargetCurrencyCode());
        if (exchangeRates.isEmpty()) {
            List<ExchangeRate> exchangeRatesUSDA = exchangeRateDao.getExchangeRateByCodes("USD", exchangeDto.getBaseCurrencyCode());
            if (exchangeRatesUSDA.isEmpty()) {
                return null;
            }
            List<ExchangeRate> exchangeRatesUSDB = exchangeRateDao.getExchangeRateByCodes("USD", exchangeDto.getTargetCurrencyCode());
            if (exchangeRatesUSDB.isEmpty()) {
                return null;
            }
            return getExchangeRateThroughCrossExchange(exchangeRatesUSDA,exchangeRatesUSDB, exchangeDto);
        } else {
            return getExchangeRateThroughDirectExchange(exchangeRates,exchangeDto);
        }
    }

    private ExchangeRateWithAmountDto getExchangeRateThroughCrossExchange (List<ExchangeRate> exchangeRatesUSDA, List<ExchangeRate> exchangeRatesUSDB, ExchangeDto exchangeDto) {
        for (ExchangeRate exchangeRateUSDA : exchangeRatesUSDA) {
            for (ExchangeRate exchangeRateUSDB : exchangeRatesUSDB){
                if (exchangeRateUSDB.getBaseCurrency().getCode().equals(exchangeDto.getTargetCurrencyCode())
                        && exchangeRateUSDA.getBaseCurrency().getCode().equals(exchangeDto.getBaseCurrencyCode())) {
                    return returnDto(exchangeRateUSDA.getBaseCurrency(),exchangeRateUSDB.getBaseCurrency(),
                            Util.divideFloat(exchangeRateUSDA.getRate(),exchangeRateUSDB.getRate()),
                            exchangeDto.getAmount(),Util.multiplyFloat(Util.multiplyFloat(exchangeDto.getAmount(),exchangeRateUSDA.getRate()),
                                    exchangeRateUSDB.getRate()));
                } else if (exchangeRateUSDB.getTargetCurrency().getCode().equals(exchangeDto.getTargetCurrencyCode())
                        && exchangeRateUSDA.getBaseCurrency().getCode().equals(exchangeDto.getBaseCurrencyCode())) {
                    return returnDto(exchangeRateUSDA.getBaseCurrency(),exchangeRateUSDB.getTargetCurrency(),
                            Util.multiplyFloat(exchangeRateUSDA.getRate(),exchangeRateUSDB.getRate()),
                            exchangeDto.getAmount(),Util.multiplyFloat(Util.multiplyFloat(exchangeDto.getAmount(),exchangeRateUSDA.getRate()),
                                    exchangeRateUSDB.getRate()));
                } else if (exchangeRateUSDB.getTargetCurrency().getCode().equals(exchangeDto.getTargetCurrencyCode())
                        && exchangeRateUSDA.getTargetCurrency().getCode().equals(exchangeDto.getBaseCurrencyCode())) {
                    return returnDto(exchangeRateUSDA.getTargetCurrency(),exchangeRateUSDB.getTargetCurrency(),
                            Util.divideFloat(exchangeRateUSDB.getRate(),exchangeRateUSDA.getRate()),
                            exchangeDto.getAmount(),Util.divideFloat(Util.multiplyFloat(exchangeDto.getAmount(),exchangeRateUSDB.getRate()),
                                    exchangeRateUSDA.getRate()));
                } else {
                    return returnDto(exchangeRateUSDA.getTargetCurrency(),exchangeRateUSDB.getBaseCurrency(),
                            Util.divideFloat(1f,Util.multiplyFloat(exchangeRateUSDB.getRate(),exchangeRateUSDA.getRate())),
                            exchangeDto.getAmount(),Util.divideFloat(Util.divideFloat(exchangeDto.getAmount(),exchangeRateUSDB.getRate()),
                                    exchangeRateUSDA.getRate()));
                }
            }
        }
        return null;
    }

    private ExchangeRateWithAmountDto getExchangeRateThroughDirectExchange (List<ExchangeRate> exchangeRates,ExchangeDto exchangeDto) {
        for (ExchangeRate exchangeRate : exchangeRates) {
            if (exchangeRate.getBaseCurrency().getCode().equals(exchangeDto.getBaseCurrencyCode())) {
                return returnDto(exchangeRate.getBaseCurrency(),exchangeRate.getTargetCurrency(),
                        exchangeRate.getRate(),exchangeDto.getAmount(),
                        Util.multiplyFloat(exchangeRate.getRate(),exchangeDto.getAmount()));
            } else {
                return returnDto(exchangeRate.getTargetCurrency(),exchangeRate.getBaseCurrency(),
                        Util.divideFloat(1f,exchangeRate.getRate()),
                        exchangeDto.getAmount(),Util.divideFloat(exchangeDto.getAmount(),exchangeRate.getRate()));

            }
        }
        return null;
    }

    public ExchangeRate getExchangeRateByCodes (String baseCurrencyCode, String targetCurrencyCode) {
        List<ExchangeRate> exchangeRates = exchangeRateDao.getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);
        if (!(exchangeRates.isEmpty())) {
            for (ExchangeRate exchangeRate : exchangeRates) {
                if (exchangeRate.getBaseCurrency().getCode().equals(baseCurrencyCode)) {
                    return exchangeRate;

                } else {
                    return exchangeRates.getFirst();

                }
            }
        }
        return null;
    }

    private ExchangeRateWithAmountDto returnDto(Currency baseCurrency, Currency tartgetCurrency, Float rate, Float amount, Float convertedAmount) {
        return ExchangeRateWithAmountDto.builder()
                .baseCurrency(Currency.builder()
                        .id(baseCurrency.getId())
                        .code(baseCurrency.getCode())
                        .fullName(baseCurrency.getFullName())
                        .sign(baseCurrency.getSign()).build())
                .targetCurrency(Currency.builder()
                        .id(tartgetCurrency.getId())
                        .code(tartgetCurrency.getCode())
                        .fullName(tartgetCurrency.getFullName())
                        .sign(tartgetCurrency.getSign()).build())
                .rate(rate)
                .amount(amount)
                .convertedAmount(convertedAmount)
                .build();
    }
}
