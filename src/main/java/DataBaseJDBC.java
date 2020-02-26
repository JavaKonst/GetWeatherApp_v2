//Сохранение города, температуры в базу данных SQLite

import java.sql.*;

public class DataBaseJDBC {
    private String city;
    private String temp;
    private String errorMsg;
    
    public String saveTemp(String inputCity, String inputTemp) {
        city = inputCity;
        temp = inputTemp;
        
        if (!dbSQLite()) return errorMsg;
        
        return "Данные записаны.";
    }
    
    private boolean dbSQLite() {
        //Локальная база данных dataOfWeather.db
        String url = "jdbc:sqlite:dataOfWeather.db";
        String query;
        
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            
            //Создание таблице если ее нет в базе данных
            query = "CREATE TABLE IF NOT EXISTS 'temp' (" +
                    "'id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "'city' VARCHAR(45) NOT NULL," +
                    "'temp' VARCHAR(4) NOT NULL);";
            
            statement.executeUpdate(query);
            
            //Внесение погоды и выбранного города в таблицу
            query = "INSERT INTO temp (city, temp) VALUES('" + city + "', '" + temp + "');";
            statement.executeUpdate(query);

        } catch (SQLException e) {
            errorMsg = "Ошибка! Что-то пошло не так при работе с SQLite: " + e.getMessage();
            return false;
        }
        return true;
    }
    
}
