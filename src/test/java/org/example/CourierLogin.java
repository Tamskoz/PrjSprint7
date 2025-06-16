package org.example;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

public class CourierLogin {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    @Step("Получение ID курьера по его login и password")
    public static int getCourierId(String login, String password) {
        Response response = given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body("{\n" + "\"login\": \"" + login + "\",\n" + "\"password\": \"" + password + "\"\n" + "}")
                .when()
                .post("/api/v1/courier/login");

        return response.path("id");
    }

    public static void main(String[] args) {

    }
}