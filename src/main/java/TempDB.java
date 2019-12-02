//Сохранение города, температуры в базу данных SQLite

import java.sql.*;

public class TempDB {
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
        String url = "jdbc:sqlite:dataOfWeather.db";         //Создание локальной базы данных dataOfWeather.db
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

//            //Просмотр данных в таблице
//            query = "SELECT * FROM temp";
//            ResultSet rs = statement.executeQuery(query);
//
//            while (rs.next()) {
//                System.out.printf("%d) В городе %s температура %s\u00B0С\n",
//                        rs.getInt("id"),
//                        rs.getString("city"),
//                        rs.getString("temp"));
//            }
            
        } catch (SQLException e) {
            errorMsg = "Ошибка! Что-то пошло не так при работе с SQLite: " + e.getMessage();
            return false;
        }
        return true;
    }
    
}
