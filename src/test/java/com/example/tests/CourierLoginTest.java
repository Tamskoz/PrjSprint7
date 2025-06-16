package com.example.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.example.CourierDeletion.deleteCourierById;
import static org.example.CourierLogin.getCourierId;
import static org.example.CreateCourier.createCourier;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru"; // Базовый URL
    }

        // 1.Тест для проверки успешной авторизация курьера
    @DisplayName("200 ОК при успешной авторизации курьера") //
    @Description("Проверка успешной авторизации курьера")
    @Test
    public void testSuccessfulLogin() {
        // Формируем уникальные значения login, password и firstName для каждого запуска
        String uniqueLogin = "TamS" + System.currentTimeMillis();
        String uniquePassword = "Pass" + System.currentTimeMillis();
        String uniqueFirstName = "FirstName" + System.currentTimeMillis();
        createCourier(uniqueLogin,uniquePassword,uniqueFirstName);//Создаем курьера

        Response response = given()
                .header("Content-Type", "application/json")
                .body("{" + "\"login\":\"" + uniqueLogin + "\"," + "\"password\":\"" + uniquePassword + "\""+"}")
                .when()
                .post("/api/v1/courier/login");

        response.then()
                .assertThat()
                .statusCode(200) // Проверяем статус 200 (успешная авторизация)
                .and()
                .body("id", notNullValue()); // Проверяем, что успешный запрос возвращается непустой id-шник

        int idCour = getCourierId(uniqueLogin, uniquePassword); // Получаем id курьера по логину и паролю
        //System.out.println(idCour);
        deleteCourierById(idCour);  // Удаляем курьера с таким id
    }

    // 2.  Проверка, что при отсутствии обязательного поля login попытка авторизации возвращает 400-сотую ошибку
    @DisplayName("400 Bad Request при отсутствии обязательного поля login")
    @Description("Нужно передавать обязательное поле login")
    @Test
    public void testMissingLoginError() {
        Response response = given()
                .header("Content-Type", "application/json")
                .body("{\n" + "\"password\": \"12TamS99934\"\n" + "}") // Пропущен login
                .when().post("/api/v1/courier/login");

        response.then()
                .assertThat()
                .statusCode(400)                  // Проверяем статус 400(Bad Request)
                .body(containsString("Недостаточно данных для входа\""));
    }

    // 3.  Проверка, что при отсутствии обязательного поля password попытка авторизации 400-сотая ошибка
    // НО тест возвращает 504-ую ошибку и получаем ошибку выполнения - НУЖЕН ДЕФЕКТ
    @DisplayName("400 Bad Request при отсутствии обязательного поля password")
    @Description("Нужно передавать обязательное поле password")
    @Test
    public void testMissingPasswordError() {
        Response response = given()
                .header("Content-Type", "application/json")
                .body("{\n" + " \"login\": \"TamS999\"}") // Пропущен password
                .when().post("/api/v1/courier/login");

        response.then()
                .assertThat()
                .statusCode(400)                  // Ожидается 400 ответ, но приходит 504 - из-за этого тест падает
                .body(containsString("Service unavailable"));
    }

    // 4.  Проверка, что при НЕверном login, но верном password попытка авторизации возвращает 404 ошибку
    @DisplayName("404 Not Found при неверном login, но верном password")
    @Description("Проверка соответствия login и password")
    @Test
    public void testInvalidLoginError() {
        //  Формируем уникальные значения login, password и firstName для каждого запуска
        String uniqueLogin = "TamS" + System.currentTimeMillis();
        String uniquePassword = "Pass" + System.currentTimeMillis();
        String uniqueFirstName = "FirstName" + System.currentTimeMillis();
        createCourier(uniqueLogin,uniquePassword,uniqueFirstName); //Создаем курьера

        Response response = given()
                .header("Content-Type", "application/json")
                .body("{" + "\"login\":\"" + "uniqueLogin" + "\"," + "\"password\":\"" + uniquePassword + "\""+"}") // неверный login, но верный password
                .when().post("/api/v1/courier/login");

        response.then()
                .assertThat()
                .statusCode(404) // Проверяем статус 404 (Not Found)
                .body(containsString("Учетная запись не найдена"));

        int idCour = getCourierId(uniqueLogin, uniquePassword); // Получаем id курьера по логину и паролю
        //System.out.println(idCour);
        deleteCourierById(idCour);  // Удаляем курьера с таким id
    }

    // 5.  Проверка, что при НЕверном password, но верном login попытка авторизации возвращает 404 ошибку
    @DisplayName("404 Not Found при неверном password, но верном login")
    @Description("Проверка соответствия login и password")
    @Test
    public void testInvalidPasswordError() {
        // Формируем уникальные значения login, password и firstName для каждого запуска
        String uniqueLogin = "TamS" + System.currentTimeMillis();
        String uniquePassword = "Pass" + System.currentTimeMillis();
        String uniqueFirstName = "FirstName" + System.currentTimeMillis();
        createCourier(uniqueLogin,uniquePassword,uniqueFirstName); //Создаем курьера

         Response response = given()
                .header("Content-Type", "application/json")
                .body("{" + "\"login\":\"" + uniqueLogin + "\"," + "\"password\":\"" + "uniquePassword" + "\""+"}") // неверный password, но верный login
                .when().post("/api/v1/courier/login");

        response.then()
                .assertThat()
                .statusCode(404) // Проверяем статус 404 (Not Found)
                .body(containsString("Учетная запись не найдена"));

        int idCour = getCourierId(uniqueLogin, uniquePassword); // Получаем id курьера по логину и паролю
        //System.out.println(idCour);
        deleteCourierById(idCour);  // Удаляем курьера с таким id
    }

    // 6.  Проверка, что при НЕсуществующем login попытка авторизации возвращает 404 ошибку
    @DisplayName("404 Not Found при НЕсуществующем login")
    @Description("Проверка наличия login в БД")
    @Test
    public void testNonExisteLoginError() {
        // Формируем уникальные значения login, password и firstName для каждого запуска
        String uniqueLogin = "TamS" + System.currentTimeMillis();
        String uniquePassword = "Pass" + System.currentTimeMillis();
        String uniqueFirstName = "FirstName" + System.currentTimeMillis();

        createCourier(uniqueLogin,uniquePassword,uniqueFirstName); //Создаем курьера

        int idCour = getCourierId(uniqueLogin, uniquePassword); // Получаем id курьера по логину и паролю для последующего удаления
        //System.out.println(idCour);
        deleteCourierById(idCour);  // Удаляем курьера с таким id

        //Делаем попытку авторизоваться с данными удаленного курьера
        Response response = given()
                .header("Content-Type", "application/json")
                .body("{" + "\"login\":\"" + uniqueLogin + "\"," + "\"password\":\"" + uniquePassword + "\""+"}")
                .when().post("/api/v1/courier/login");

        response.then()
                .assertThat()
                .statusCode(404) // Проверяем статус 404 (Not Found)
                .body(containsString("Учетная запись не найдена"));
    }
}
