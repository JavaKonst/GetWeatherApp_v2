//Получение координат(широта и долгота) введенного города в сервисе Yandex.Geocoder

import jdk.nashorn.internal.parser.JSONParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONString;

import java.util.Arrays;

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

        //Формирование запроса для получения широты и долготы города
        HttpGet request = new HttpGet(geocoderURL + "?format=json&apikey=" + accessKey + "&geocode=" + cityInput);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                errorMsg = "Ошибка! Сервис YandexGeocoder не отвечает.";
                return false;
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String data = EntityUtils.toString(entity);
                String findedCity;

                System.out.println(data);

                JSONObject object = new JSONObject(data);

                JSONObject step0 = object.getJSONObject("response");
                JSONObject step1 = step0.getJSONObject("GeoObjectCollection");
                JSONArray step2 = (JSONArray) step1.get("featureMember");
                JSONObject step3 = (JSONObject) step2.get(0);
                JSONObject step4 = step3.getJSONObject("GeoObject");
                System.out.println(step4.getString("name")+" ("+step4.getString("description")+")");
                //String data1 = "{\"response\":{\"GeoObjectCollection\":{\"metaDataProperty\":{\"GeocoderResponseMetaData\":{\"request\":\"минск\",\"results\":\"10\",\"found\":\"4\"}},\"featureMember\":[{\"GeoObject\":{\"metaDataProperty\":{\"GeocoderMetaData\":{\"precision\":\"other\",\"text\":\"Беларусь, Минск\",\"kind\":\"locality\",\"Address\":{\"country_code\":\"BY\",\"formatted\":\"Беларусь, Минск\",\"Components\":[{\"kind\":\"country\",\"name\":\"Беларусь\"},{\"kind\":\"province\",\"name\":\"Минск\"},{\"kind\":\"locality\",\"name\":\"Минск\"}]},\"AddressDetails\":{\"Country\":{\"AddressLine\":\"Беларусь, Минск\",\"CountryNameCode\":\"BY\",\"CountryName\":\"Беларусь\",\"AdministrativeArea\":{\"AdministrativeAreaName\":\"Минск\",\"Locality\":{\"LocalityName\":\"Минск\"}}}}}},\"name\":\"Минск\",\"description\":\"Беларусь\",\"boundedBy\":{\"Envelope\":{\"lowerCorner\":\"27.374416 53.79388\",\"upperCorner\":\"28.080303 53.97162\"}},\"Point\":{\"pos\":\"27.561481 53.902496\"}}},{\"GeoObject\":{\"metaDataProperty\":{\"GeocoderMetaData\":{\"precision\":\"other\",\"text\":\"Россия, Красноярский край, Большемуртинский район, деревня Минск\",\"kind\":\"locality\",\"Address\":{\"country_code\":\"RU\",\"formatted\":\"Россия, Красноярский край, Большемуртинский район, деревня Минск\",\"Components\":[{\"kind\":\"country\",\"name\":\"Россия\"},{\"kind\":\"province\",\"name\":\"Сибирский федеральный округ\"},{\"kind\":\"province\",\"name\":\"Красноярский край\"},{\"kind\":\"area\",\"name\":\"Большемуртинский район\"},{\"kind\":\"locality\",\"name\":\"деревня Минск\"}]},\"AddressDetails\":{\"Country\":{\"AddressLine\":\"Россия, Красноярский край, Большемуртинский район, деревня Минск\",\"CountryNameCode\":\"RU\",\"CountryName\":\"Россия\",\"AdministrativeArea\":{\"AdministrativeAreaName\":\"Красноярский край\",\"SubAdministrativeArea\":{\"SubAdministrativeAreaName\":\"Большемуртинский район\",\"Locality\":{\"LocalityName\":\"деревня Минск\"}}}}}}},\"name\":\"деревня Минск\",\"description\":\"Большемуртинский район, Красноярский край, Россия\",\"boundedBy\":{\"Envelope\":{\"lowerCorner\":\"93.326029 57.095468\",\"upperCorner\":\"93.348029 57.101213\"}},\"Point\":{\"pos\":\"93.336952 57.098304\"}}},{\"GeoObject\":{\"metaDataProperty\":{\"GeocoderMetaData\":{\"precision\":\"other\",\"text\":\"Беларусь, Минск, Национальный аэропорт Минск\",\"kind\":\"airport\",\"Address\":{\"country_code\":\"BY\",\"formatted\":\"Беларусь, Минск, Национальный аэропорт Минск\",\"Components\":[{\"kind\":\"country\",\"name\":\"Беларусь\"},{\"kind\":\"province\",\"name\":\"Минск\"},{\"kind\":\"airport\",\"name\":\"Национальный аэропорт Минск\"}]},\"AddressDetails\":{\"Country\":{\"AddressLine\":\"Беларусь, Минск, Национальный аэропорт Минск\",\"CountryNameCode\":\"BY\",\"CountryName\":\"Беларусь\",\"AdministrativeArea\":{\"AdministrativeAreaName\":\"Минск\",\"Locality\":{\"DependentLocality\":{\"DependentLocalityName\":\"Национальный аэропорт Минск\"}}}}}}},\"name\":\"Национальный аэропорт Минск\",\"description\":\"Минск, Беларусь\",\"boundedBy\":{\"Envelope\":{\"lowerCorner\":\"27.999625 53.86235\",\"upperCorner\":\"28.077904 53.912929\"}},\"Point\":{\"pos\":\"28.033824 53.889216\"}}},{\"GeoObject\":{\"metaDataProperty\":{\"GeocoderMetaData\":{\"precision\":\"street\",\"text\":\"Молдова, Кишинёв, улица Минск\",\"kind\":\"street\",\"Address\":{\"country_code\":\"MD\",\"formatted\":\"Молдова, Кишинёв, улица Минск\",\"Components\":[{\"kind\":\"country\",\"name\":\"Молдова\"},{\"kind\":\"area\",\"name\":\"муниципий Кишинёв\"},{\"kind\":\"locality\",\"name\":\"Кишинёв\"},{\"kind\":\"street\",\"name\":\"улица Минск\"}]},\"AddressDetails\":{\"Country\":{\"AddressLine\":\"Молдова, Кишинёв, улица Минск\",\"CountryNameCode\":\"MD\",\"CountryName\":\"Молдова\",\"AdministrativeArea\":{\"AdministrativeAreaName\":\"муниципий Кишинёв\",\"Locality\":{\"LocalityName\":\"Кишинёв\",\"Thoroughfare\":{\"ThoroughfareName\":\"улица Минск\"}}}}}}},\"name\":\"улица Минск\",\"description\":\"Кишинёв, Молдова\",\"boundedBy\":{\"Envelope\":{\"lowerCorner\":\"28.860759 46.997519\",\"upperCorner\":\"28.87038 47.001803\"}},\"Point\":{\"pos\":\"28.865529 46.999738\"}}}]}}}";

//                JSONPointer pointer = new JSONPointer("/response");

//                JSONObject response1 = object.getJSONObject("response");
//                JSONObject geoObjectCollection = response1.getJSONObject("GeoObjectCollection");
//
//                JSONArray featureMember = geoObjectCollection.getJSONArray("featureMember");
//                System.out.println(featureMember.length()+"-------"+featureMember.toString()); //4

//                JSONObject geoObject = featureMember.getJSONObject(0);
//                System.out.println(geoObject.names().toString());
//                System.out.println(featureMember.getJSONObject(1).names().toString());
//
//                System.out.println("");
//                System.out.println(featureMember.get(0).toString());
//                System.out.println("");
//
//                JSONObject geoObject = featureMember.getJSONObject(0);
////                JSONArray array = geoObject.getJSONArray("metaDataProperty");
////                System.out.println(array.toString());
//                String str = geoObject.getString("name");
//                System.out.println(str);


//                JSONObject meta = geoObject.getJSONObject("metaDataProperty");
//                JSONObject geocoder = meta.getJSONObject("GeocoderMetaData");

//                JSONObject metaDataProperty = featureMember..getJSONObject("metaDataProperty");
//                JSONObject geocoderMetaData = metaDataProperty.getJSONObject("GeocoderMetaData");
//                String request1 = geocoderMetaData.getString("text");
//                System.out.println("---: "+ request1);


//                System.out.println("== " + gson.toString());
//                System.out.println("== "+dataJson.toString());
                while (entity != null){
                 Thread.sleep(1000);
                }

                //Если найдено записей 0, то выход из функции с ошибкой
                if (Integer.parseInt(data.substring(data.indexOf("<found>") + 7, data.indexOf("</found>"))) == 0) {
                    errorMsg = "Ошибка! Город не найден.";
                    return false;
                }

                //Если найденный город не совпадает с введенным, то выход из функции с ошибкой
                findedCity = data.substring(data.indexOf("<formatted>") + 11, data.indexOf("</formatted>"));
                if (!cityInput.equalsIgnoreCase(findedCity.substring(findedCity.lastIndexOf(" ") + 1))) {
                    errorMsg = "Ошибка! Город не найден.";
                    return false;
                }

                //Форматирование строки: "Город (Cтрана)"
                String[] cityNames = findedCity.split(", ");
                cityOutput = Character.toUpperCase(cityOutput.charAt(0)) + cityOutput.substring(1) + " (" + cityNames[0] + ")";

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
