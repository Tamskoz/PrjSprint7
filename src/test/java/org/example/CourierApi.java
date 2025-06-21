package org.example;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;

public class CourierApi {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    @Step("Получение ID курьера по его login и password ")
    public static int getCourierId(String login, String password) {

        CourierLoginRequest requestBody = new CourierLoginRequest(login, password);

        Response response = given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login");

        // assertThat(response.getStatusCode(), equalTo(SC_OK));
        if ((response.getStatusCode() == SC_CREATED) || (response.getStatusCode() == SC_OK)) {
            return response.path("id");
        } else {
            return 0;
        }
    }

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

    //  Метод создания нового курьера
    @Step("Создание нового курьера с параметрами login, password, firstName")
       public static Response createCourier(CreateCourierRequest requestBody) {
        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/courier");
    }

    //  Метод для авторизации курьера
    @Step("Авторизация курьера с параметрами login, password")
    public static Response LoginCourier(CourierLoginRequest requestBody) {
        Response response = given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login");
        return response;
    }

    //Mетод для получение списка заказов курьера
    @Step("Получение списка заказов курьера")
    public static Response СourierOrderList(int courierId) {

        Response response = given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .get("/api/v1/orders?courierId=" + courierId);
        return response;
    }
      /*  public static void main(String[] args) {
        }*/

}
