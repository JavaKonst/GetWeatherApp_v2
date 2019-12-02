//Основные действия

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String city = "Москва";
        String[] lon_lat;
        String temp;
        int numService;
        String isSave;
        String isStop = "да";
        Scanner scanner = new Scanner(System.in);

        while (isStop.equalsIgnoreCase("да")) {
            System.out.print("Введите город: ");
            if (scanner.hasNext()) city = scanner.nextLine();

            LatLonCity latLonCity = new LatLonCity();
            lon_lat = latLonCity.getLatLon(city);
            if (lon_lat[0].contains("Ошибка")) {
                System.out.println(lon_lat[0]);
                System.out.print("\nПродолжить (да/нет)?  --> ");
                if (scanner.hasNext()) isStop = scanner.nextLine();
                System.out.println("\n------------------\n\n");
                continue;
            }

            city = latLonCity.getFullCityName();

            System.out.print("Выберите погодный сервис:" +
                    "\n\t1) Yandex.Weather" +
                    "\n\t2) AccuWeather" +
                    "\n\t3) OpenWeather\n");
            System.out.print("--> ");

            String in = "";
            while (true) {
                if (scanner.hasNext()) in = scanner.nextLine();
                if (in.equals("1") || in.equals("2") || in.equals("3")) break;
                else System.out.print("Ошибка! Повторите свой выбор:\n--> ");
            }

            numService = Integer.parseInt(in);

            temp = new WeatherServices().getCityTemp(lon_lat[1], lon_lat[0], numService);

            if (temp.contains("Ошибка")) {
                System.out.println(temp);
                System.out.print("\nПродолжить (да/нет)?  --> ");
                if (scanner.hasNext()) isStop = scanner.nextLine();
                System.out.println("\n------------------\n\n");
                continue;
            }

            isSave = new TempDB().saveTemp(city, temp);

            if (isSave.contains("Ошибка")) {
                System.out.println(isSave);
                System.out.print("\nПродолжить (да/нет)?  --> ");
                if (scanner.hasNext()) isStop = scanner.nextLine();
                System.out.println("\n------------------\n\n");
                continue;
            }
            System.out.println("ОТВЕТ: Температура в городе " + city + ": " + temp + "\n");

            System.out.print("Продолжить (да/нет)?  --> ");
            if (scanner.hasNext()) isStop = scanner.nextLine();
            System.out.println("\n------------------\n");
        }
    }
}
