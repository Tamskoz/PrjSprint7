package org.example;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class CourierDeletion {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    // Метод удаляет  курьера по его id.
    @Step("Удаление курьера по courierId")
    public static Response deleteCourierById(int courierId) {
        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .pathParam("courierId", courierId)
                .when()
                .delete("/api/v1/courier/{courierId}");

    }

    public static void main(String[] args) {
    }
}