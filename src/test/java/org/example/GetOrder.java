package org.example;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class GetOrder {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

   // Метод для создания закада и получения его track
   @Step("Создание заказа и получение track-кода")
    public static int createOrderAndGetTrack() {
        // Запрос JSON тела
        String requestBody = "{\n" +
                "    \"firstName\": \"Татьяна\",\n" +
                "    \"lastName\": \"Козлова\",\n" +
                "    \"address\": \"Москва, Красная площадь, д.1\",\n" +
                "    \"metroStation\": \"Краснопресненская\",\n" +
                "    \"phone\": \"+7 900 009 09 09\",\n" +
                "    \"rentTime\": 14,\n" +
                "    \"deliveryDate\": \"2025-06-15\",\n" +
                "    \"comment\": \"Будьте осторожны!!!\",\n" +
                "    \"color\": [\n" +
                "        \"BLACK\"\n" +
                "    ]\n" +
                "}";

        Response response =
                RestAssured.given()
                        .baseUri("https://qa-scooter.praktikum-services.ru")
                        .basePath("/api/v1/")
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .post("orders");

        int statusCode = response.getStatusCode();

       if (statusCode != 201) { //  Проверка успешного статуса HTTP
            throw new RuntimeException("Ошибка создания заказа! Код состояния: " + statusCode);
        }

        return response.path("track");  // Получаем трек заказа из ответа
    }
    public static void main(String[] args) {
    }
}


