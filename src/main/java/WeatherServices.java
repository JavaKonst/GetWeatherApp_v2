import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class WeatherServices {
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private String errorMsg;
    private String cityTemp;
    private String lat;
    private String lon;

    public String getCityTemp(String cityLAT, String cityLON, int numService) {
        lat = cityLAT;
        lon = cityLON;

        switch (numService) {
            case 1:
                if (!yandexWeather()) return errorMsg;
                break;
            case 2:
                if (!accuWeather()) return errorMsg;
                break;
            case 3:
                if (!openWeather()) return errorMsg;
                break;

            default:
                return "Ошибка! Сервис не найден. Выберите другой.";
        }

        return cityTemp;

    }

    public boolean yandexWeather() {
        String weatherURL = "https://api.weather.yandex.ru/v1/forecast";
        String accessKey = "a9dac15c-cc86-462c-afce-7db15f8fb962";                 //Временный ключ доступа к сервису

        //Формирование запроса: координаты города (долгота и широта), русский язык. Плюс добавление хэдэра с ключом доступа
        HttpGet request = new HttpGet(weatherURL + "?lat=" + lat + "&lon=" + lon + "&lang=ru_RU");
        request.addHeader("X-Yandex-API-Key", accessKey);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                errorMsg = "Ошибка! Сервис YandexWeather выдал код ответа: " + response.getStatusLine().getStatusCode() + ".";
                return false;
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String data = EntityUtils.toString(entity);
                int temp;

                JSONObject object = new JSONObject(data);
                JSONObject fact = object.getJSONObject("fact");
                temp = fact.getInt("temp");

                if (temp > 0) cityTemp = "+" + temp + "\u00B0C";
                else cityTemp = temp + "\u00B0C";
            } else {
                errorMsg = "Ошибка! Сервис YandexWeather выдал пустые данные.";
                return false;
            }
        } catch (Exception e) {
            errorMsg = "Ошибка! Что-то пошло не так при работе с YandexWeather: " + e.getMessage();
            return false;
        }
        return true;
    }

    public boolean accuWeather() {
        String cityIdURL = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search";
        String weatherURL = "http://dataservice.accuweather.com/forecasts/v1/hourly/1hour/";
        String accessKey = "nNrlkATNJUU5AB2Qj9iHm6wmC3MMrEDC";
        String cityID;

        //Формирование запроса для получения уникального номера города cityID
        HttpGet request = new HttpGet(cityIdURL + "?apikey=" + accessKey + "&q=" + lat + "%2C%20" + lon + "&language=ru&toplevel=true");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                errorMsg = "Ошибка! Сервис AccuWeather выдал код ответа: " + response.getStatusLine().getStatusCode() + ".";
                return false;
            }

            HttpEntity entity = response.getEntity();

            //Обработка полученных данных
            if (entity != null) {
                String data = EntityUtils.toString(entity);

                JSONObject object = new JSONObject(data);
                cityID = object.getString("Key");

//                cityID = data.substring(data.indexOf("Key") + 6, data.indexOf("Type") - 3);
            } else {
                errorMsg = "Ошибка! Сервис AccuWeather выдал пустые данные.";
                return false;
            }
        } catch (Exception e) {
            errorMsg = "Ошибка! Что-то пошло не так при работе с AccuWeather: " + e.getMessage();
            return false;
        }

        //Формирование запроса для получения информации о погоде в городе под номером cityID
        request = new HttpGet(weatherURL + cityID + "?apikey=" + accessKey + "&q=" + lat + "%2C%20" + lon + "&language=RU&metric=TRUE");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                errorMsg = "Ошибка! Сервис AccuWeather выдал код ответа: " + response.getStatusLine().getStatusCode() + ".";
                return false;
            }

            HttpEntity entity = response.getEntity();

            //Обработка полученных данных
            if (entity != null) {
                String data = EntityUtils.toString(entity);

                data = data.substring(1,data.length()-1);

                JSONObject object = new JSONObject(data);
                JSONObject temperature = object.getJSONObject("Temperature");
                int temp = temperature.getInt("Value");

                data = data.substring(data.indexOf("Value") + 7, data.indexOf("Unit") - 2);
                data = data.substring(0, data.indexOf("."));
                if (Integer.parseInt(data) > 0) cityTemp = "+" + data + "\u00B0C";
                else cityTemp = data + "\u00B0C";
                if (temp > 0) cityTemp = "+" + temp + "\u00B0C";
                else cityTemp = temp + "\u00B0C";

            } else {
                errorMsg = "Ошибка! Сервис AccuWeather выдал пустые данные.";
                return false;
            }
        } catch (Exception e) {
            errorMsg = "Ошибка! Что-то пошло не так при работе с AccuWeather: " + e.getMessage();
            return false;
        }

        return true;
    }

    public boolean openWeather() {
        String weatherURL = "http://api.openweathermap.org/data/2.5/weather";
        String accessKey = "f5838bdccfef720169d8613fe0f8d0ad";

        //Формирование запроса: координаты города (долгота и широта), ключ доступа, формат вывода xml, единицы Градусы, русский язык
        HttpGet request = new HttpGet(weatherURL + "?lat=" + lat + "&lon=" + lon + "&APPID=" + accessKey + "&units=metric&lang=ru");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                errorMsg = "Ошибка! Сервис OpenWeather выдал код ответа: " + response.getStatusLine().getStatusCode() + ".";
                return false;
            }
            HttpEntity entity = response.getEntity();

            //Обработка полученных данных
            if (entity != null) {
                String data = EntityUtils.toString(entity);

                JSONObject object = new JSONObject(data);
                JSONObject main = object.getJSONObject("main");
                int temp = main.getInt("temp");

                if (temp > 0) cityTemp = "+" + temp + "\u00B0C";
                else cityTemp = temp + "\u00B0C";

            } else {
                errorMsg = "Ошибка! Сервис OpenWeather выдал пустые данные.";
                return false;
            }
        } catch (Exception e) {
            errorMsg = "Ошибка! Что-то пошло не так при работе с OpenWeather: " + e.getMessage();
            return false;
        }

        return true;
    }
}
