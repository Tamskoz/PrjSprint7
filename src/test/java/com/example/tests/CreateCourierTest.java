package com.example.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.example.CourierDeletion.deleteCourierById;
import static org.example.CourierLogin.getCourierId;
import static org.hamcrest.Matchers.*;

public class CreateCourierTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru"; // Базовый URL
    }

    // 1. Тест на успешное создание курьера с уникальными правильными параметрами 201 Created
    @DisplayName("201 Created при успешном создании курьера") //
    @Description("Успешое создание курьера с уникальными значениями атрибутов login, password и firstName")
    @Test
    public void createCourierSuccessfully() {

        // Формируем уникальные значения login, password и firstName для каждого запуска
        String uniqueLogin = "TamS" + System.currentTimeMillis();
        String uniquePassword = "Pass" + System.currentTimeMillis();
        String uniqueFirstName = "FirstName" + System.currentTimeMillis();

        // Формируем тело запроса
        String json  = String.format("{ \"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\" }",
                uniqueLogin, uniquePassword, uniqueFirstName);

        given()
                .cookie("_yasc", "x2/xImZllCoQhKbJTj2oWu+iHvex3DSKXZtEP6SWokdcZfzVz5wTdkVIIzn3hOlQwQ==")
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/v1/courier")
                .then()
                .assertThat()
                .statusCode(201)               // Проверяем статус 201 (успешное создание)
                .body("ok", equalTo(true));    // Проверяем, что успешный запрос возвращает ok: true;

        int idCour = getCourierId(uniqueLogin, uniquePassword); // Получаем id курьера по логину и паролю
        //System.out.println(idCour);
        deleteCourierById(idCour);  // Удаляем курьера с таким id
    }

    // 2. Повторное создание курьера с теми же данными возвращает ошибку 409 Conflict
    // Этот тест так же проверяет, что при создании курьера с таким же логином получем 409 ошибку
    @DisplayName("409 Conflict при попытке создать двух одинаковых курьеров")
    @Description("Нельзя создать двух курьеров с одними и теми же данными")
    @Test
    public void cannotCreateSameCourier() {

        // Формируем уникальные значения login, password и firstName для каждого запуска
        String existingLogin = "TamsExisting" + System.currentTimeMillis();
        String existingPassword = "PasExisting" + System.currentTimeMillis();
        String existingFirstName = "FirstNameExisting" + System.currentTimeMillis();

        // Первый раз создаем курьера
        String json = String.format("{ \"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\" }",
                existingLogin, existingPassword, existingFirstName);

        given()
                .cookie("_yasc", "x2/xImZllCoQhKbJTj2oWu+iHvex3DSKXZtEP6SWokdcZfzVz5wTdkVIIzn3hOlQwQ==")
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/v1/courier")
                .then()
                .assertThat()
                .statusCode(201); // Курьер успешно создан

        // Пробуем создать курьера с теми же данными
        given()
                .cookie("_yasc", "x2/xImZllCoQhKbJTj2oWu+iHvex3DSKXZtEP6SWokdcZfzVz5wTdkVIIzn3hOlQwQ==")
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/v1/courier")
                .then()
                .assertThat()
                .statusCode(409)
                .body(containsString("Этот логин уже используется. Попробуйте другой."));// Проверяем статус 409 (Conflict)

        int idCour = getCourierId(existingLogin, existingPassword); // Получаем id курьера по логину и паролю
        //System.out.println(idCour);
        deleteCourierById(idCour);  // Удаляем курьера с таким id

    }

    // 3. Проверка, что при отсутствии обязательного поля password возвращается 400-сотая ошибка
    @DisplayName("400 Bad Request при отсутствии обязательного поля password")
    @Description("Нужно передавать обязательное поле password")
    @Test
    public void missingPasswordReturnsError() {
        String json = "{ \"login\": \"MissingPassword\", \"firstName\": \"FirstName\" }";

        given()
                .cookie("_yasc", "x2/xImZllCoQhKbJTj2oWu+iHvex3DSKXZtEP6SWokdcZfzVz5wTdkVIIzn3hOlQwQ==")
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/v1/courier")
                .then()
                .assertThat()
                .statusCode(400)                  // Проверяем статус 400(Bad Request)
                .body(containsString("Недостаточно данных для создания учетной записи"));
    }

    // 4. Проверка, что при отсутствии обязательного поля login возвращается 400-сотая ошибка
    @DisplayName("400 Bad Request при отсутствии обязательного поля login")
    @Description("Нужно передавать обязательное поле login")
    @Test
    public void missingLoginReturnsError() {
        String json = "{ \"password\": \"Password\", \"firstName\": \"MissingLogin\" }";

        given()
                .cookie("_yasc", "x2/xImZllCoQhKbJTj2oWu+iHvex3DSKXZtEP6SWokdcZfzVz5wTdkVIIzn3hOlQwQ==")
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/v1/courier")
                .then()
                .assertThat()
                .statusCode(400)                  // Проверяем статус 400(Bad Request)
                .body(containsString("Недостаточно данных для создания учетной записи"));
    }

    // 5. Проверка, что при отсутствии НЕобязательного поля firstName курьер успешно создается
    @DisplayName("201 Created при отсутствии НЕобязательного поля firstName")
    @Description("Успешное создание курьера без НЕобязательного поля firstName")
    @Test
    public void createCourierNoFirstNameSuccessfully() {

        // Формируем уникальные значения login и password
        String uniqueLogin = "TamS32" + System.currentTimeMillis();
        String uniquePassword = "Pass" + System.currentTimeMillis();

        // Формируем тело запроса
        String json  = String.format("{ \"login\": \"%s\", \"password\": \"%s\"}",
                uniqueLogin, uniquePassword);

        given()
                .cookie("_yasc", "x2/xImZllCoQhKbJTj2oWu+iHvex3DSKXZtEP6SWokdcZfzVz5wTdkVIIzn3hOlQwQ==")
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/v1/courier")
                .then()
                .assertThat()
                .statusCode(201)               // Проверяем статус 201 (успешное создание)
                .body("ok", equalTo(true));    // Проверяем, что успешный запрос возвращает ok: true;

        int idCour = getCourierId(uniqueLogin, uniquePassword); // Получаем id курьера по логину и паролю
        //System.out.println(idCour);
        deleteCourierById(idCour);  // Удаляем курьера с таким id
    }
}


