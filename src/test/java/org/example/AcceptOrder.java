package org.example;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class AcceptOrder {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    //  Метод для принятия заказа курьером
    @Step("Принятие заказа курьером с courierId")
    public static Response acceptOrder(Integer orderId, Integer courierId) {
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .cookie("_yasc", "U5512Vl2re1QqIbDNoFGX5rtpAi40gjRwPJaIy70IaU4oKYg2VWvO8rvCgWf6xm9fQ==") // Куки
                .queryParam("courierId", courierId)      // Передача параметра courierId
                .when()
                .put("/api/v1/orders/accept/" + orderId); // Выполняем PUT-запрос
    }

    public static void main(String[] args) {

    }
}
