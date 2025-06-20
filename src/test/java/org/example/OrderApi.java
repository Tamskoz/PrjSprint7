package org.example;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderApi {

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

    //  Метод для отмены заказа по track-номеру
    @Step("Отменяем заказ по его trackId")
    public static Response cancelOrder(int trackNumber) {
        return given()
                .baseUri(BASE_URL)
                .accept(ContentType.JSON)
                .queryParam("track", trackNumber)          // Параметр запроса (номер заказа)
                .when()
                .put("/api/v1/orders/cancel");            // Отправляем PUT-запрос
    }

    //  Метод для принятия заказа курьером
    @Step("Принятие заказа курьером с courierId")
    public static Response acceptOrder(Integer orderId, Integer courierId) {
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("courierId", courierId)      // Передача параметра courierId
                .when()
                .put("/api/v1/orders/accept/" + orderId); // Выполняем PUT-запрос
    }

    //  Метод для получения заказа по track и возвращения его ID
    @Step("Получение ID заказа по его trackId")
    public static Integer getOrderIdByTrack(int trackNumber) {
        return given()
                .baseUri(BASE_URL)
                .accept(ContentType.JSON)
                .queryParam("t", trackNumber)          // Передаем параметр track
                .when()
                .get("/api/v1/orders/track")           // Выполняем GET-запрос
                .then()
                .extract()                             // Извлекаем из ответа
                .path("order.id");                     // Возвращаем значение пути "order.id"
    }

    public static void main(String[] args) {
    }
}


