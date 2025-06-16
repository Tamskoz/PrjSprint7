package org.example;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class CreateOrder {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    // Метод для создания заказа и получения поля track
    public Integer createOrderAndGetTrack() {
        // Создаем объект заказа класса Order
        Order orderData = new Order(
                "Татьяна",
                "Козлова",
                "Москва, Красная площадь, д.1",
                "Краснопресненская",
                "+7 900 009 09 09",
                14,
                "2025-06-15",
                "Будьте осторожны!!!",
                List.of("BLACK")); // Цвет задан списком строк

        // Выполнеем POST-запрос создания заказа и получение ответа
        Response response = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .cookie("_yasc", "RpYf4TTDZ2NrFpEzIYmgHm+K4kCj6zk82X33PQzHGUlRATwpUAFNrl+kaYyOigKO+g==")
                .body(orderData)
                .when()
                .post("/api/v1/orders");

        // Извлекаем целое число из поля "track"
        return response.then().extract().path("track");
    }

}
