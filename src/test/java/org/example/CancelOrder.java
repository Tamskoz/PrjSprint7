package org.example;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class CancelOrder {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    //  Метод для отмены заказа по track-номеру
    @Step("Отменяем заказ по его trackId")
    public static Response cancelOrder(int trackNumber) {
        return given()
                .baseUri(BASE_URL)
                .accept(ContentType.JSON)
                .cookie("_yasc", "U5512Vl2re1QqIbDNoFGX5rtpAi40gjRwPJaIy70IaU4oKYg2VWvO8rvCgWf6xm9fQ==")
                .queryParam("track", trackNumber)          // Параметр запроса (номер заказа)
                .when()
                .put("/api/v1/orders/cancel");            // Отправляем PUT-запрос
    }
    public static void main(String[] args) {

    }
}