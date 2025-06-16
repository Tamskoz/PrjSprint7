package com.example.tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.example.CancelOrder.cancelOrder;
import static org.example.GetOrderByTrack.getOrderIdByTrack;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class СreateOrderTest {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    // 1. Тест для проверки успешного создания заказа, если указан один из цветов BLACK или GREY
    @ParameterizedTest
    @ValueSource(strings = {"BLACK", "GREY"})
    @DisplayName("201 Created при указании одного из цветов BLACK или GREY") //
    @Description("Проверка успешногосоздания заказа, если указан один из цветов BLACK или GREY")
    void testBlackOrGrayColor(String color) {
        String json = "{" +
                "  \"firstName\": \"Татьяна\"," +
                "  \"lastName\": \"Козлова\"," +
                "  \"address\": \"Москва, Красная площадь, д.1\"," +
                "  \"metroStation\": \"Краснопресненская\"," +
                "  \"phone\": \"+7 900 009 09 09\"," +
                "  \"rentTime\": 19," +
                "  \"deliveryDate\": \"2025-06-15\"," +
                "  \"comment\": \"Будьте осторожны!!!\"," +
                "  \"color\": [\"%s\"]" +
                "}";

        String formattedJson = String.format(json, color); // Заменяем "%s" значением color

        Response response =  given()
                .baseUri(BASE_URL)
                .headers("Content-Type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/orders");
        response.then()
                .assertThat()
                .statusCode(201) // Проверяем статус 201 (успешное создание)
                .and()
                .body(containsString("track")); // Проверяем наличие "track" в теле успешного ответа

        int strOrd = getOrderIdByTrack(response.path("track")); // Получаем id заказа по номеру трека
        //System.out.println(strOrd);
        cancelOrder(strOrd); //Отменяем заказ по id
    }


    // 2. Тест для проверки успешного создания заказа, если указаны оба цвета BLACK и GREY
    @DisplayName("201 Created при указании обоих цветов BLACK и GREY") //
    @Description("Проверка успешного создания заказа, если указаны оба цвета BLACK и GREY")
    @Test
    void testBlackAndGrayColor() {

        String json = "{" +
                "  \"firstName\": \"Татьяна\"," +
                "  \"lastName\": \"Козлова\"," +
                "  \"address\": \"Москва, Красная площадь, д.1\"," +
                "  \"metroStation\": \"Краснопресненская\"," +
                "  \"phone\": \"+7 900 009 09 09\"," +
                "  \"rentTime\": 19," +
                "  \"deliveryDate\": \"2025-06-15\"," +
                "  \"comment\": \"Будьте осторожны!!!\"," +
                "  \"color\": [\"BLACK\", \"GREY\"]" +
                "}";

        Response response =  given()
                .baseUri(BASE_URL)
                .headers("Content-Type", "application/json")
                .body(json)
                .when().post("/api/v1/orders");
        response.then()
                .assertThat()
                .statusCode(201) // Проверяем статус 201 (успешное создание)
                .and()
                .body(containsString("track")); // Проверяем наличие "track" в теле успешного ответа

        int strOrd = getOrderIdByTrack(response.path("track")); // Получаем id заказа по номеру трека
        //System.out.println(strOrd);
        cancelOrder(strOrd); //Отменяем заказ по id
    }

    // 3. Тест для проверки успешного создания заказа, БЕЗ указания цвета
    @DisplayName("201 Created без указания цвета") //
    @Description("Проверка успешного создания заказа, без параметра color")
    @Test
    void testNoColor() {

        String json = "{" +
                "  \"firstName\": \"Татьяна\"," +
                "  \"lastName\": \"Козлова\"," +
                "  \"address\": \"Москва, Красная площадь, д.1\"," +
                "  \"metroStation\": \"Краснопресненская\"," +
                "  \"phone\": \"+7 900 009 09 09\"," +
                "  \"rentTime\": 12," +
                "  \"deliveryDate\": \"2025-06-16\"," +
                "  \"comment\": \"Будьте осторожны!!!\"" +
                "}";

        Response response =  given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                //.header("Cookie", "_yasc=MNXsWKWg6uZdtAYgqal+NUCRLE+ZpGZQbtKfjEhIHFpwTy8mPF0Kvy/irzYYsz3iPA==")
                .body(json)
                .when()
                .post("/api/v1/orders");
        response.then()
                .assertThat()
                .statusCode(201) // Проверяем статус 201 (успешное создание)
                .and()
                .body(containsString("track")); // Проверяем наличие "track" в теле успешного ответа

        int strOrd = getOrderIdByTrack(response.path("track")); // Получаем id заказа по номеру трека
        //System.out.println(strOrd);
        cancelOrder(strOrd); //Отменяем заказ по id
    }
}
