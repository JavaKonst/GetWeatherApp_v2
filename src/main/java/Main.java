//Основные действия

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String cityFromConsole = "abc";
        String cityFromQuery;
        String[] lon_lat;
        String tempFromQuery;
        int numService;
        String isSave;
        String isStop = "да";
        Scanner scanner = new Scanner(System.in);

        while (isStop.equalsIgnoreCase("да")) {
            System.out.print("Введите город: ");
            while (scanner.hasNext()) {
                cityFromConsole = scanner.nextLine().trim();
                if (!cityFromConsole.equals("")) break;
            }

            LatLonCity latLonCity = new LatLonCity();
            lon_lat = latLonCity.getLatLon(cityFromConsole);
            if (lon_lat[0].contains("Ошибка")) {
                System.out.println(lon_lat[0]);
                System.out.print("\nПродолжить (да/нет)?  --> ");
                if (scanner.hasNext()) isStop = scanner.nextLine();
                System.out.println("\n------------------\n\n");
                continue;
            } else cityFromQuery = latLonCity.getFullCityName();

            System.out.print("Выберите погодный сервис:" +
                    "\n\t1) Yandex.Weather" +
                    "\n\t2) AccuWeather" +
                    "\n\t3) OpenWeather\n");
            System.out.print("--> ");

            String in = "";
            while (true) {
                if (scanner.hasNext()) in = scanner.nextLine().trim();
                if (in.equals("1") || in.equals("2") || in.equals("3")) break;
                else System.out.print("Ошибка! Повторите свой выбор:\n--> ");
            }

            numService = Integer.parseInt(in);

            tempFromQuery = new WeatherServices().getCityTemp(lon_lat[1], lon_lat[0], numService);

            if (tempFromQuery.contains("Ошибка")) {
                System.out.println(tempFromQuery);
                System.out.print("\nПродолжить (да/нет)?  --> ");
                if (scanner.hasNext()) isStop = scanner.nextLine();
                System.out.println("\n------------------\n\n");
                continue;
            }

            isSave = new DataBaseJDBC().saveTemp(cityFromQuery, tempFromQuery);

            if (isSave.contains("Ошибка")) {
                System.out.println(isSave);
                System.out.print("\nПродолжить (да/нет)?  --> ");
                if (scanner.hasNext()) isStop = scanner.nextLine();
                System.out.println("\n------------------\n\n");
                continue;
            }
            System.out.println("ОТВЕТ: Температура в городе " + cityFromQuery + ": " + tempFromQuery + "\n");

            System.out.print("Продолжить (да/нет)?  --> ");
            if (scanner.hasNext()) isStop = scanner.nextLine();
            System.out.println("\n------------------\n");
        }
    }
}
