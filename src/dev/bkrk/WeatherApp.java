package dev.bkrk;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {

    // fetch weather data for given location.
    public static JSONObject getWeatherData(String locationName) {
        // get location coordinates with the using geolocation API.
        JSONArray locationData = getLocationData(locationName);

        // extract coordinates.
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // build API request URL with coordinates.
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude="+ latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto";
        try {
            // call api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);
            // check for response status, 200 means connection was a success.
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Couldn't connect to API");
                return null;
            }
            // store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                // read and store into the stringBuilder.
                resultJson.append(scanner.nextLine());
            }
            // close scanner and connection.
            scanner.close();
            conn.disconnect();

            // parse data.
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // get hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            // we want to get the current hour's data
            // so we need to get the index of our current hour.
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOurCurrentTime(time);

            // get temperature.
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            // get humidity.
            JSONArray relativeHumidity =(JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get wind_speed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            // build the weather JSON data object for the front-end.
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLocationData(String locationName) {
        // Replace spaces with "+" for API query.
        locationName = locationName.replaceAll(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn.getResponseCode() != 200) {
                JOptionPane.showMessageDialog(
                        null,
                        "<html>API connection failed." +
                                "<br>Please check your internet connection<br>" +
                                "and make sure you only use English characters.</html>",
                        "API Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return null;
            } else {
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }
                scanner.close();
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
                JSONArray locationData = (JSONArray) resultJsonObj.get("results");

                if (locationData == null || locationData.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "No location found for the given name. Please try again.",
                            "Location Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return null;
                }

                return locationData;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred while fetching the location data. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace(); // Optional: Remove this if you don't want to log the stack trace.
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            // create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // set request method to get.
            conn.setRequestMethod("GET");
            // connect to our API
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // couldn't make connection.
        return null;
    }

    private static int findIndexOurCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime() {
        // get current date and time.
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format to be YYYY-MM-DDT00:00 (API read type.)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and print the current date and time.
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    // convert the weather code to more readable.
    private static String convertWeatherCode(long weatherCode){
        String weatherCondition = "";
        if(weatherCode == 0L){
            // clear
            weatherCondition = "Clear";
        }else if(weatherCode > 0L && weatherCode <= 3L){
            // cloudy
            weatherCondition = "Cloudy";
        }else if((weatherCode >= 51L && weatherCode <= 67L)
                || (weatherCode >= 80L && weatherCode <= 99L)){
            // rain
            weatherCondition = "Rain";
        }else if(weatherCode >= 71L && weatherCode <= 77L){
            // snow
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}
