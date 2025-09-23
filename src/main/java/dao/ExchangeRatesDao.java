package dao;

import dto.ExchangeRateDto;
import dto.ExchangeRatesDto;
import entities.Currency;
import entities.ExchangeRate;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ExchangeRatesDao {

    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static Connection connection;

    private static final String ER_ID = "ER_id";
    private static final String BC_ID = "BC_id";
    private static final String BC_CODE = "BC_code";
    private static final String BC_FULL_NAME = "BC_full_name";
    private static final String BC_SIGN = "BC_sign";

    private static final String TC_ID = "TC_id";
    private static final String TC_CODE = "TC_code";
    private static final String TC_FULL_NAME = "TC_full_name";
    private static final String TC_SIGN = "TC_sign";
    private static final String RATE = "rate";
    static {
        try {
            connection = DriverManager.getConnection(URL);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new liquibase.database.jvm.JdbcConnection(connection));

            CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
            updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database);
            updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, "db/changelog/db.changelog-master.yaml");
            updateCommand.execute();
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ExchangeRate> findAllExchangeRates(){
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT er.ID as ER_id, c1.ID as BC_id, c1.CODE as BC_code, c1.FULL_NAME as BC_full_name, c1.SIGN as BC_sign,"+
                          "c2.ID as TC_id, c2.CODE as TC_code, c2.FULL_NAME as TC_full_name, c2.SIGN as TC_sign,"+
                          "er.RATE as rate FROM CURRENCIES_TABLE as c1 JOIN EXCHANGE_RATES_TABLE as er " +
                          "ON c1.ID=er.BASE_CURRENCY_ID JOIN CURRENCIES_TABLE as c2 ON c2.ID=er.TARGET_CURRENCY_ID";
            ResultSet resultSet = statement.executeQuery(SQL);

            while (resultSet.next()) {

                exchangeRates.add(ExchangeRate.builder()
                        .id(resultSet.getLong(ER_ID))
                        .baseCurrency(Currency.builder()
                                .id(resultSet.getLong(BC_ID))
                                .code(resultSet.getString(BC_CODE))
                                .fullName(resultSet.getString(BC_FULL_NAME))
                                .sign(resultSet.getString(BC_SIGN))
                                .build())
                        .targetCurrency(Currency.builder()
                                .id(resultSet.getLong(TC_ID))
                                .code(resultSet.getString(TC_CODE))
                                .fullName(resultSet.getString(TC_FULL_NAME))
                                .sign(resultSet.getString(TC_SIGN))
                                .build())
                        .rate(resultSet.getFloat(RATE))
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        return exchangeRates;
    }

    public List<ExchangeRate> getExchangeRateByCodes(String baseCurrencyCode, String targetCurrencyCode) {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try {
            String SQL = "SELECT er.ID as ER_id, c1.ID as BC_id, c1.CODE as BC_code, c1.FULL_NAME as BC_full_name, c1.SIGN as BC_sign,"+
                    "c2.ID as TC_id, c2.CODE as TC_code, c2.FULL_NAME as TC_full_name, c2.SIGN as TC_sign,"+
                    "er.RATE as rate FROM CURRENCIES_TABLE as c1 JOIN EXCHANGE_RATES_TABLE as er " +
                    "ON c1.ID=er.BASE_CURRENCY_ID JOIN CURRENCIES_TABLE as c2 ON c2.ID=er.TARGET_CURRENCY_ID WHERE (c1.CODE=? AND c2.CODE=?) OR (c1.CODE=? AND c2.CODE=?)";
            PreparedStatement preparedStatement = connection.prepareStatement (SQL);
            preparedStatement.setString(1,baseCurrencyCode);
            preparedStatement.setString(2,targetCurrencyCode);
            preparedStatement.setString(3,targetCurrencyCode);
            preparedStatement.setString(4,baseCurrencyCode);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                exchangeRates.add(ExchangeRate.builder()
                        .id(resultSet.getLong(ER_ID))
                        .baseCurrency(Currency.builder()
                                .id(resultSet.getLong(BC_ID))
                                .code(resultSet.getString(BC_CODE))
                                .fullName(resultSet.getString(BC_FULL_NAME))
                                .sign(resultSet.getString(BC_SIGN))
                                .build())
                        .targetCurrency(Currency.builder()
                                .id(resultSet.getLong(TC_ID))
                                .code(resultSet.getString(TC_CODE))
                                .fullName(resultSet.getString(TC_FULL_NAME))
                                .sign(resultSet.getString(TC_SIGN))
                                .build())
                        .rate(resultSet.getFloat(RATE))
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRates;
    }

    public void addExchangeRate (ExchangeRatesDto exchangeRateDto) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO EXCHANGE_RATES_TABLE VALUES(DEFAULT, " +
                                                    "(SELECT ID FROM CURRENCIES_TABLE WHERE CODE=?), " +
                                                    "(SELECT ID FROM CURRENCIES_TABLE WHERE CODE=?), ?)");
            preparedStatement.setString(1, exchangeRateDto.getBaseCurrencyCode());
            preparedStatement.setString(2, exchangeRateDto.getTargetCurrencyCode());
            preparedStatement.setFloat(3, exchangeRateDto.getRate());
            preparedStatement.executeUpdate();
            connection.commit();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int updateExchangeRate (ExchangeRateDto exchangeRateDto) {
        try {
            String SQL = "UPDATE EXCHANGE_RATES_TABLE SET RATE=? WHERE " +
                    "(BASE_CURRENCY_ID=(SELECT c1.ID FROM CURRENCIES_TABLE as c1 WHERE c1.CODE=?) " +
                    "AND TARGET_CURRENCY_ID=(SELECT c2.ID FROM CURRENCIES_TABLE as c2 WHERE c2.CODE=?))" +
                    "OR (BASE_CURRENCY_ID=(SELECT c3.ID FROM CURRENCIES_TABLE as c3 WHERE c3.CODE=?) " +
                    "AND TARGET_CURRENCY_ID=(SELECT c4.ID FROM CURRENCIES_TABLE as c4 WHERE c4.CODE=?))";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setFloat(1,exchangeRateDto.getRate());
            preparedStatement.setString(2,exchangeRateDto.getBaseCurrencyCode());
            preparedStatement.setString(3,exchangeRateDto.getTargetCurrencyCode());
            preparedStatement.setString(4,exchangeRateDto.getTargetCurrencyCode());
            preparedStatement.setString(5,exchangeRateDto.getBaseCurrencyCode());
            int rowsUpdated = preparedStatement.executeUpdate();
            connection.commit();
            return rowsUpdated;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "ExchangeRatesDao{}";
    }
}
