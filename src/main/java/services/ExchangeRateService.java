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
            for (ExchangeRate exchangeRateUSDA : exchangeRatesUSDA) {
                for (ExchangeRate exchangeRateUSDB : exchangeRatesUSDB){
                    if (exchangeRateUSDB.getBaseCurrency().getCode().equals(exchangeDto.getTargetCurrencyCode())
                     && exchangeRateUSDA.getBaseCurrency().getCode().equals(exchangeDto.getBaseCurrencyCode())) {
                        return ExchangeRateWithAmountDto.builder()
                                .baseCurrency(Currency.builder()
                                        .id(exchangeRateUSDA.getBaseCurrency().getId())
                                        .code(exchangeRateUSDA.getBaseCurrency().getCode())
                                        .fullName(exchangeRateUSDA.getBaseCurrency().getFullName())
                                        .sign(exchangeRateUSDA.getBaseCurrency().getSign()).build())
                                .targetCurrency(Currency.builder()
                                        .id(exchangeRateUSDB.getBaseCurrency().getId())
                                        .code(exchangeRateUSDB.getBaseCurrency().getCode())
                                        .fullName(exchangeRateUSDB.getBaseCurrency().getFullName())
                                        .sign(exchangeRateUSDB.getBaseCurrency().getSign()).build())
                                .rate(Util.divideFloat(exchangeRateUSDA.getRate(),exchangeRateUSDB.getRate()))
                                .amount(exchangeDto.getAmount())
                                .convertedAmount(Util.divideFloat(Util.multiplyFloat(exchangeDto.getAmount(),exchangeRateUSDA.getRate()),
                                                                                    exchangeRateUSDB.getRate()))
                                .build();
                    } else if (exchangeRateUSDB.getTargetCurrency().getCode().equals(exchangeDto.getTargetCurrencyCode())
                            && exchangeRateUSDA.getBaseCurrency().getCode().equals(exchangeDto.getBaseCurrencyCode())) {
                        return ExchangeRateWithAmountDto.builder()
                                .baseCurrency(Currency.builder()
                                        .id(exchangeRateUSDA.getBaseCurrency().getId())
                                        .code(exchangeRateUSDA.getBaseCurrency().getCode())
                                        .fullName(exchangeRateUSDA.getBaseCurrency().getFullName())
                                        .sign(exchangeRateUSDA.getBaseCurrency().getSign()).build())
                                .targetCurrency(Currency.builder()
                                        .id(exchangeRateUSDB.getTargetCurrency().getId())
                                        .code(exchangeRateUSDB.getTargetCurrency().getCode())
                                        .fullName(exchangeRateUSDB.getTargetCurrency().getFullName())
                                        .sign(exchangeRateUSDB.getTargetCurrency().getSign()).build())
                                .rate(Util.multiplyFloat(exchangeRateUSDA.getRate(),exchangeRateUSDB.getRate()))
                                .amount(exchangeDto.getAmount())
                                .convertedAmount(Util.multiplyFloat(Util.multiplyFloat(exchangeDto.getAmount(),exchangeRateUSDA.getRate()),
                                                                                      exchangeRateUSDB.getRate()))
                                .build();
                    } else if (exchangeRateUSDB.getTargetCurrency().getCode().equals(exchangeDto.getTargetCurrencyCode())
                            && exchangeRateUSDA.getTargetCurrency().getCode().equals(exchangeDto.getBaseCurrencyCode())) {
                        return ExchangeRateWithAmountDto.builder()
                                .baseCurrency(Currency.builder()
                                        .id(exchangeRateUSDA.getTargetCurrency().getId())
                                        .code(exchangeRateUSDA.getTargetCurrency().getCode())
                                        .fullName(exchangeRateUSDA.getTargetCurrency().getFullName())
                                        .sign(exchangeRateUSDA.getTargetCurrency().getSign()).build())
                                .targetCurrency(Currency.builder()
                                        .id(exchangeRateUSDB.getTargetCurrency().getId())
                                        .code(exchangeRateUSDB.getTargetCurrency().getCode())
                                        .fullName(exchangeRateUSDB.getTargetCurrency().getFullName())
                                        .sign(exchangeRateUSDB.getTargetCurrency().getSign()).build())
                                .rate(Util.divideFloat(exchangeRateUSDB.getRate(),exchangeRateUSDA.getRate()))
                                .amount(exchangeDto.getAmount())
                                .convertedAmount(Util.divideFloat(Util.multiplyFloat(exchangeDto.getAmount(),exchangeRateUSDB.getRate()),
                                                                                    exchangeRateUSDA.getRate()))
                                .build();
                    } else {
                        return ExchangeRateWithAmountDto.builder()
                                .baseCurrency(Currency.builder()
                                        .id(exchangeRateUSDA.getTargetCurrency().getId())
                                        .code(exchangeRateUSDA.getTargetCurrency().getCode())
                                        .fullName(exchangeRateUSDA.getTargetCurrency().getFullName())
                                        .sign(exchangeRateUSDA.getTargetCurrency().getSign()).build())
                                .targetCurrency(Currency.builder()
                                        .id(exchangeRateUSDB.getBaseCurrency().getId())
                                        .code(exchangeRateUSDB.getBaseCurrency().getCode())
                                        .fullName(exchangeRateUSDB.getBaseCurrency().getFullName())
                                        .sign(exchangeRateUSDB.getBaseCurrency().getSign()).build())
                                .rate(Util.divideFloat(1f,Util.multiplyFloat(exchangeRateUSDB.getRate(),exchangeRateUSDA.getRate())))
                                .amount(exchangeDto.getAmount())
                                .convertedAmount(Util.divideFloat(Util.divideFloat(exchangeDto.getAmount(),exchangeRateUSDB.getRate()),
                                                                                  exchangeRateUSDA.getRate()))
                                .build();
                    }
                }
            }
        } else {
            for (ExchangeRate exchangeRate : exchangeRates) {
                if (exchangeRate.getBaseCurrency().getCode().equals(exchangeDto.getBaseCurrencyCode())) {
                    return ExchangeRateWithAmountDto.builder()
                            .baseCurrency(Currency.builder()
                                    .id(exchangeRate.getBaseCurrency().getId())
                                    .code(exchangeRate.getBaseCurrency().getCode())
                                    .fullName(exchangeRate.getBaseCurrency().getFullName())
                                    .sign(exchangeRate.getBaseCurrency().getSign()).build())
                            .targetCurrency(Currency.builder()
                                    .id(exchangeRate.getTargetCurrency().getId())
                                    .code(exchangeRate.getTargetCurrency().getCode())
                                    .fullName(exchangeRate.getTargetCurrency().getFullName())
                                    .sign(exchangeRate.getTargetCurrency().getSign()).build())
                            .rate(exchangeRate.getRate())
                            .amount(exchangeDto.getAmount())
                            .convertedAmount(Util.multiplyFloat(exchangeRate.getRate(),exchangeDto.getAmount()))
                            .build();
                } else {
                    return ExchangeRateWithAmountDto.builder()
                            .baseCurrency(Currency.builder()
                                    .id(exchangeRate.getTargetCurrency().getId())
                                    .code(exchangeRate.getTargetCurrency().getCode())
                                    .fullName(exchangeRate.getTargetCurrency().getFullName())
                                    .sign(exchangeRate.getTargetCurrency().getSign()).build())
                            .targetCurrency(Currency.builder()
                                    .id(exchangeRate.getBaseCurrency().getId())
                                    .code(exchangeRate.getBaseCurrency().getCode())
                                    .fullName(exchangeRate.getBaseCurrency().getFullName())
                                    .sign(exchangeRate.getBaseCurrency().getSign()).build())
                            .rate(Util.divideFloat(1f,exchangeRate.getRate()))
                            .amount(exchangeDto.getAmount())
                            .convertedAmount(Util.divideFloat(exchangeDto.getAmount(),exchangeRate.getRate()))
                            .build();

                }
            }

        }
        return null;
    }

}
