//Основные действия

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        String city = "Москва";
        String[] lon_lat;
        String temp;
        int numService = 1;
        String isSave;
        String isStop = "да";
        Scanner scanner = new Scanner(System.in);


        while (isStop.equalsIgnoreCase("да")) {


            System.out.print("Введите город: ");
            if(scanner.hasNext()) city = scanner.nextLine();

            System.out.print("Выберите погодный сервис:" +
                    "\n\t1) Yandex.Weather" +
                    "\n\t2) AccuWeather" +
                    "\n\t3) OpenWeather\n");
            System.out.print("--> ");

            if(scanner.hasNext()) numService = scanner.nextInt();

            LatLonCity latLonCity = new LatLonCity();
            lon_lat = latLonCity.getLatLon(city);
            if (lon_lat[0].contains("Ошибка")) {
                System.out.println(lon_lat[0]);
                System.out.print("\nПродолжить (да/нет)?  --> ");
                scanner.nextLine();
                if(scanner.hasNext()) isStop = scanner.nextLine();
                System.out.println("\n------------------\n\n");
                continue;
            }

            city = latLonCity.getFullCityName();

            temp = new WeatherServices().getCityTemp(lon_lat[1], lon_lat[0], numService);

            if (temp.contains("Ошибка")) {
                System.out.println(temp);
                System.out.print("\nПродолжить (да/нет)?  --> ");
                scanner.nextLine();
                if(scanner.hasNext()) isStop = scanner.nextLine();
                System.out.println("\n------------------\n\n");
                continue;
            }

            isSave = new TempDB().saveTemp(city, temp);

            if (isSave.contains("Ошибка")) {
                System.out.println(isSave);
                System.out.print("\nПродолжить (да/нет)?  --> ");
                scanner.nextLine();
                if(scanner.hasNext()) isStop = scanner.nextLine();
                System.out.println("\n------------------\n\n");
                continue;
            }
            System.out.println("ОТВЕТ: Температура в городе " + city + ": " + temp + "\n\n");
            
            System.out.print("Продолжить (да/нет)?  --> ");
            scanner.nextLine();
            if(scanner.hasNext()) isStop = scanner.nextLine();
            System.out.println("\n------------------\n");
        }
    }
}
