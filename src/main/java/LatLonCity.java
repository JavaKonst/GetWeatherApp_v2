//Получение координат(широта и долгота) введенного города в сервисе Yandex.Geocoder

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class LatLonCity {
    private String city;
    private String errorMsg;
    private String[] lon_lat = new String[2];
    
    public String[] getLatLon(String selectCity) {
        city = selectCity;
        
        if (!yandexGeocoder()) lon_lat[0] = errorMsg;
        
        return lon_lat;
    }
    
    private boolean yandexGeocoder() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String accessKey = "2bd70c9e-0d97-4e45-b9fb-e1cec797488e";                            //Временный ключ доступа к сервису, тариф Тестовый
        String geocoderURL = "https://geocode-maps.yandex.ru/1.x/";
        
        //Формирование запроса для получения широты и долготы города
        HttpGet request = new HttpGet(geocoderURL + "?apikey=" + accessKey + "&geocode=" + city);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                errorMsg = "Ошибка! Сервис YandexGeocoder не отвечает.";
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
                if (!city.equalsIgnoreCase(findedCity.substring(findedCity.lastIndexOf(" ") + 1))) {
                    errorMsg = "Ошибка! Город не найден.";
                    return false;
                }
                
                //Форматирование строки: "Город (Cтрана)"
                String[] cityNames = findedCity.split(", ");
                city = Character.toUpperCase(city.charAt(0)) + city.substring(1) + " (" + cityNames[0] + ")";
                
                //Поиск и получение координат города
                data = data.substring(data.indexOf("<pos>") + 5, data.indexOf("</pos>"));
                lon_lat = data.split(" ");
            } else {
                errorMsg = "Ошибка! Сервис YandexGeocoder выдал пустые данные.";
                return false;
            }
        } catch (Exception e) {
            errorMsg = "Ошибка! Что-то пошло не так при работе с YandexGeocoder: " + e.getMessage();
            return false;
        }
        return true;
    }
    
}
