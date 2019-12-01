import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class TempraryClass {

//    public class Weather {
        private static CloseableHttpClient httpClient = HttpClients.createDefault();
        private static String citySelect = "да";
        private static String cityTemp;
        private static String cityLat;
        private static String cityLon;
        private static String errorMsg;
        private static boolean isExit;
        private static boolean isChange = true;
        private static boolean status;
        private static int numService;

        public static void main(String[] args) {
            Weather app = new Weather();
            Scanner scanner = new Scanner(System.in);

            while (!isExit) {
                if (isChange) {
                    System.out.println("Доступны след. сервисы:");
                    System.out.println("1. Yandex.Погода");
                    System.out.println("2. AccuWeather");
                    System.out.println("3. OpenWeather");
                    System.out.print("\nВыберите номер сервиса: ");
                    if (scanner.hasNext()) numService = scanner.nextInt();
                }


                System.out.print("Введите город: ");
                if (scanner.hasNext()) citySelect = scanner.next();

                if (!app.getCityLAT_LON()) System.out.println(errorMsg);
                else {
                    switch (numService){
                        case 1:
                            status = app.getCityTempYa();
                            break;
                        case 2:
                            status = app.getCityTempAccu();
                            break;
                        case 3:
                            status = app.getCityTempOpenW();
                            break;
                        default:
                            break;
                    }
                    if (!status) System.out.println(errorMsg);
                    else if (!app.saveToDB()) System.out.println(errorMsg);
                }

                System.out.println("Продолжить (Да/Нет)?");
                if (scanner.hasNext()) {
                    isExit = !scanner.next().equalsIgnoreCase("да");
                    if (isExit) break;
                }

                System.out.println("Сменить погодный сервис (Да/Нет)?");
                if (scanner.hasNext()) isChange = scanner.next().equalsIgnoreCase("да");
            }
        }

        //Из сервиса yandex.geocoder получаем координаты введенного города
        private boolean getCityLAT_LON() {
            String key = "2bd70c9e-0d97-4e45-b9fb-e1cec797488e";                            //Временный ключ доступа к сервису, тариф Тестовый
            String geocoderURL = "https://geocode-maps.yandex.ru/1.x/";
            HttpGet request = new HttpGet(geocoderURL + "?apikey=" + key + "&geocode=" + citySelect);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    errorMsg = "Ошибка! Сервис Yandex.geocoder не работает.";
                    return false;
                }
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String data = EntityUtils.toString(entity);
                    String findedCity;

                    //Если найдено записей 0, то выход из функции с ошибкой
                    if (Integer.parseInt(data.substring(data.indexOf("<found>") + 7, data.indexOf("</found>"))) == 0) {
                        errorMsg = "Ошибка! Город не найден.";
                        return false;
                    }

                    //Если найденный город не совпадает с введенным, то выход из функции с ошибкой
                    findedCity = data.substring(data.indexOf("<formatted>") + 11, data.indexOf("</formatted>"));
                    if (!citySelect.equalsIgnoreCase(findedCity.substring(findedCity.lastIndexOf(" ")+1))) {
                        errorMsg = "Ошибка! Город не найден.";
                        return false;
                    }

                    //Форматирование строки: "Город (Cтрана)"
                    String[] cityNames = findedCity.split(", ");
                    citySelect = Character.toUpperCase(citySelect.charAt(0)) + citySelect.substring(1) + " (" + cityNames[0] + ")";

                    //Поиск и получение координат города
                    data = data.substring(data.indexOf("<pos>") + 5, data.indexOf("</pos>"));
                    String[] cityLatLon = data.split(" ");
                    cityLon = cityLatLon[0];
                    cityLat = cityLatLon[1];
                } else {
                    errorMsg = "Ошибка! Сервис Yandex.geocoder выдал пустые данные.";
                    return false;
                }
            } catch (Exception e) {
                errorMsg = "Ошибка! Не удалось подключиться к сервису Yandex.geocoder.";
                return false;
            }
            return true;
        }

        //Из сервиса yandex.weather получаем  температуру в выбранном городе
        private boolean getCityTempYa() {
            String weatherURL = "https://api.weather.yandex.ru/v1/forecast";
            String keyWeather = "a9dac15c-cc86-462c-afce-7db15f8fb962";                 //Временный ключ доступа к сервису

            HttpGet request = new HttpGet(weatherURL + "?lat=" + cityLat + "&lon=" + cityLon + "&lang=ru_RU");
            request.addHeader("X-Yandex-API-Key", keyWeather);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String data = EntityUtils.toString(entity);

                    int tempStart = data.indexOf("temp") + 6;
                    int tempEnd = data.indexOf("feels") - 2;

                    data = data.substring(tempStart, tempEnd);
                    if (Integer.parseInt(data) > 0) cityTemp = "+" + data + "\u00B0C";
                    else cityTemp = data + "\u00B0C";

                    System.out.println("Температура в городе " + citySelect + ": " + cityTemp);
                } else {
                    errorMsg = "Ошибка! Сервис Yandex.weather выдал пустые данные.";
                    return false;
                }
            } catch (Exception e) {
                errorMsg = "Ошибка! Не удалось подключиться к сервису Yandex.weather.";
                e.printStackTrace();
                return false;
            }
            return true;
        }

        //Из сервиса AccuWeather получаем  температуру в выбранном городе
        private boolean getCityTempAccu() {
            String getCityKeyURL = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search";
            String weatherURL = "http://dataservice.accuweather.com/forecasts/v1/hourly/1hour/";
            String keyOffAccu = "nNrlkATNJUU5AB2Qj9iHm6wmC3MMrEDC";
            String keyLocation;

            //Получение уникального номера города keyLocation
            HttpGet request = new HttpGet(getCityKeyURL + "?apikey=" + keyOffAccu + "&q=" + cityLat + "%2C%20" + cityLon + "&language=ru&toplevel=true");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String data = EntityUtils.toString(entity);
                    keyLocation = data.substring(data.indexOf("Key") + 6, data.indexOf("Type") - 3);
//                citySelect = data.substring(data.indexOf("LocalizedName") + 16, data.indexOf("EnglishName") - 3);
                } else {
                    errorMsg = "Ошибка! Сервис AccuWeather выдал пустые данные.";
                    return false;
                }
            } catch (Exception e) {
                errorMsg = "Ошибка! Не удалось подключиться к сервису AccuWeather.";
                return false;
            }

            //Получение информации о погоде в городе под номером keyLocation
            request = new HttpGet(weatherURL + keyLocation + "?apikey=" + keyOffAccu + "&q=" + cityLat + "%2C%20" + cityLon + "&language=RU&metric=TRUE");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String data = EntityUtils.toString(entity);
                    data = data.substring(data.indexOf("Value") + 7, data.indexOf("Unit") - 2);
                    data = data.substring(0, data.indexOf("."));
                    if (Integer.parseInt(data) > 0) cityTemp = "+" + data + "\u00B0C";
                    else cityTemp = data + "\u00B0C";
                    System.out.println("Температура в городе " + citySelect + ": " + cityTemp);
                } else {
                    errorMsg = "Ошибка! Сервис AccuWeather выдал пустые данные.";
                    return false;
                }
            } catch (Exception e) {
                errorMsg = "Ошибка! Не удалось подключиться к сервису AccuWeather.";
                return false;
            }

            return true;
        }

        //Из сервиса OpenWeather получаем  температуру в выбранном городе
        private boolean getCityTempOpenW() {
            String weatherURL = "http://api.openweathermap.org/data/2.5/weather";
            String keyOfOpenW = "f5838bdccfef720169d8613fe0f8d0ad";

            //Запрос: координаты города (долгота и широта), ключ доступа, формат вывода xml, единицы Градусы, русский язык
            HttpGet request = new HttpGet(weatherURL + "?lat=" + cityLat + "&lon=" + cityLon +"&APPID=" +keyOfOpenW+"&mode=xml&units=metric&lang=ru");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String data = EntityUtils.toString(entity);
                    int temp;

                    data = data.substring(data.indexOf("temperature value"), data.indexOf("min"));
                    data = data.substring(data.indexOf("\"")+1, data.lastIndexOf("\""));
                    temp = (int)Double.parseDouble(data);
                    if (temp > 0) cityTemp = "+" + temp + "\u00B0C";
                    else cityTemp = temp + "\u00B0C";
                    System.out.println("Температура в городе " + citySelect + ": " + cityTemp);

                } else {
                    errorMsg = "Ошибка! Сервис OpenWeather выдал пустые данные.";
                    return false;
                }
            } catch (Exception e) {
                errorMsg = "Ошибка! Не удалось подключиться к сервису OpenWeather.";
                e.printStackTrace();
                return false;
            }

            return true;
        }

        //Сохранение данных погоды и города в локальную базу данных (SQLite)
        private boolean saveToDB() {
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
                query = "INSERT INTO temp (city, temp) VALUES('" + citySelect + "', '" + cityTemp + "');";
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
                e.printStackTrace();
                return false;
            }
            return true;
        }
//    }

}
