package dao;

import dto.CurrencyDto;
import entities.Currency;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class CurrencyDao {

    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static Connection connection;

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

    public List<Currency> findAllCurrencies() {
        List<Currency> currencies = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT * FROM CURRENCIES_TABLE";
            ResultSet resultSet = statement.executeQuery(SQL);

            while (resultSet.next()) {

                currencies.add(Currency.builder()
                        .id(resultSet.getLong("ID"))
                        .code(resultSet.getString("CODE"))
                        .fullName(resultSet.getString("FULL_NAME"))
                        .sign(resultSet.getString("SIGN"))
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return currencies;
    }

    public void addCurrency(CurrencyDto currencyDto) {

        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO CURRENCIES_TABLE VALUES(DEFAULT, ?, ?, ?)");

            preparedStatement.setString(1, currencyDto.getCode());
            preparedStatement.setString(2, currencyDto.getFullName());
            preparedStatement.setString(3, currencyDto.getSign());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Currency getCurrencyByCode (String code) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement ("SELECT * FROM CURRENCIES_TABLE WHERE CODE=?");

            preparedStatement.setString(1,code);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
               return Currency.builder()
                        .id(resultSet.getLong("ID"))
                        .code(resultSet.getString("CODE"))
                        .fullName(resultSet.getString("FULL_NAME"))
                        .sign(resultSet.getString("SIGN"))
                        .build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String toString() {
        return "CurrencyDao{}";
    }

}
