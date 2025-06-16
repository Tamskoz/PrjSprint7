package org.example;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class GetOrderByTrack {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    // Метод для получения заказа по track и возвращения его ID
    @Step("Получение ID заказа по его trackId")
    public static Integer getOrderIdByTrack(int trackNumber) {
        return given()
                .baseUri(BASE_URL)
                .accept(ContentType.JSON)
                .cookie("_yasc", "U5512Vl2re1QqIbDNoFGX5rtpAi40gjRwPJaIy70IaU4oKYg2VWvO8rvCgWf6xm9fQ==")
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
