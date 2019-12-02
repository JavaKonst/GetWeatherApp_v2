//Основные действия

import java.util.Scanner;

public class Main {
    private static String city;
    private static String[] lon_lat;
    private static String temp;
    private static int numService;
    private static String isStop = "да";
    private static String isSave;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (isStop.equalsIgnoreCase("да")) {
            System.out.println("Введите город: ");
            if (scanner.hasNext()) city = scanner.nextLine();
            else city = "Москва";
            
            System.out.println("Погодные сервисы:" +
                    "1. Yandex.Weather" +
                    "2. AccuWeather" +
                    "3. OpenWeather");
            System.out.println("Выберите номер сервиса: ");
            
            if (scanner.hasNext()) numService = scanner.nextInt();
            else numService = 1;
            
            lon_lat = new LatLonCity().getLatLon(city);
            if (lon_lat[0].contains("Ошибка")) {
                System.out.println(lon_lat[0]);
                break;
            }
            
            temp = new WeatherServices().getCityTemp(lon_lat[1], lon_lat[0], numService);
            
            if (temp.contains("Ошибка")) {
                System.out.println(temp);
                break;
            }
            
            isSave = new TempDB().saveTemp(city, temp);
            
            if (isSave.contains("Ошибка")) {
                System.out.println(isSave);
                break;
            }
            
            System.out.println("Температура в городе " + city + ": " + temp + "\n");
            
            System.out.println("Продолжить (да/нет)?");
            
            if (scanner.hasNext()) isStop = scanner.nextLine();
            else isStop = "да";
            
        }
        
    }
    
    
}
