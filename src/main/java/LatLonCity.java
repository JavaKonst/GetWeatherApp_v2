//Получение координат(широта и долгота) введенного города в сервисе Yandex.Geocoder

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class LatLonCity {
    private String cityInput;
    private String cityOutput;
    private String errorMsg;
    private String[] lon_lat = new String[2];

    public String[] getLatLon(String cityFromConsole) {
        cityInput = cityFromConsole;

        if (!yandexGeocoder()) lon_lat[0] = errorMsg;

        return lon_lat;
    }

    public String getFullCityName() {
        return cityOutput;
    }

    private boolean yandexGeocoder() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String accessKey = "2bd70c9e-0d97-4e45-b9fb-e1cec797488e";                            //Временный ключ доступа к сервису, тариф Тестовый
        String geocoderURL = "https://geocode-maps.yandex.ru/1.x/";

        //Формирование запроса для получения широты и долготы города в формате Json
        HttpGet request = new HttpGet(geocoderURL + "?format=json&apikey=" + accessKey + "&geocode=" + cityInput);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                errorMsg = "Ошибка! Сервис YandexGeocoder выдал код ответа: "+ response.getStatusLine().getStatusCode() + ".";
                return false;
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String data = EntityUtils.toString(entity);

                JSONObject object = new JSONObject(data);
                JSONObject jsonResponse = object.getJSONObject("response");
                JSONObject jsonGeoObjCol = jsonResponse.getJSONObject("GeoObjectCollection");
                JSONObject jsonMetaDataProp = jsonGeoObjCol.getJSONObject("metaDataProperty");
                JSONObject jsonGeocoderMetaData = jsonMetaDataProp.getJSONObject("GeocoderResponseMetaData");
                String foundedItems = jsonGeocoderMetaData.getString("found");

                //Если найдено записей 0, то выход из функции с ошибкой
                if (foundedItems.equals("0")) {
                    errorMsg = "Ошибка! Город не найден.";
                    return false;
                }

                JSONArray jsonFeatureMember = (JSONArray) jsonGeoObjCol.get("featureMember");
                JSONObject jsonNum0 = (JSONObject) jsonFeatureMember.get(0);
                JSONObject jsonGeoObj = jsonNum0.getJSONObject("GeoObject");
                JSONObject jsonPoint = jsonGeoObj.getJSONObject("Point");
                String point = jsonPoint.getString("pos");
                cityOutput = jsonGeoObj.getString("name");

                //Если найденный город не совпадает с введенным, то выход из функции с ошибкой
                if (!cityInput.equalsIgnoreCase(cityOutput)) {
                    errorMsg = "Ошибка! Город не найден.";
                    return false;
                }

                cityOutput = cityOutput + " ("+jsonGeoObj.getString("description")+")";

                //Поиск и получение координат города
                lon_lat = point.split(" ");
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
