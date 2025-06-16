package org.example;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class CreateCourier {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

   // Метод создания нового курьера
   @Step("Создание нового курьера с параметрами login, password, firstName")
    public static Response createCourier(String login, String password, String firstName) {
        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body("{" + "\"login\":\"" + login + "\"," + "\"password\":\"" + password + "\"," + "\"firstName\":\"" + firstName + "\"" + "}")
                .when()
                .post("/api/v1/courier");
    }

    public static void main(String[] args) {

    }
}

