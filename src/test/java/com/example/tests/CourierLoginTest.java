package com.example.tests;

import io.restassured.response.Response;
import org.example.CourierLoginRequest;
import org.junit.jupiter.api.*;
import io.qameta.allure.Description;

import static org.apache.http.HttpStatus.*;
import static io.restassured.RestAssured.given;
import static org.example.CourierApi.*;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest {
    private int courierId; // Поле для хранения идентификатора курьера

    private String uniqueLogin = "";
    private String uniquePassword = "";
    private String uniqueFirstName = "";

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    // Создание курьера перед каждым тестом
    @BeforeEach
    void setUp() {
         uniqueLogin = "TamS" + System.currentTimeMillis();
         uniquePassword = "Pass" + System.currentTimeMillis();
         uniqueFirstName = "FirstName" + System.currentTimeMillis();
        createCourier(uniqueLogin, uniquePassword, uniqueFirstName); // Создаем курьера
        courierId = getCourierId(uniqueLogin, uniquePassword); // Сохраняем созданный ID
    }

    // Удаление курьера после каждого теста
    @AfterEach
    void tearDown() {
        if (courierId != 0) { // Проверяем наличие ID перед удалением
            deleteCourierById(courierId); // Удаляем курьера
        }
    }
        // 1.Тест для проверки успешной авторизация курьера
        @DisplayName("200 ОК при успешной авторизации курьера") //
        @Description("Проверка успешной авторизации курьера")
        @Test
        public void testSuccessfulLogin () {

            CourierLoginRequest requestBody = new CourierLoginRequest(uniqueLogin, uniquePassword);

            Response response =  given()
                    .baseUri(BASE_URL)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .when()
                    .post("/api/v1/courier/login");

            response.then()
                    .log().all()
                    .assertThat()
                    .statusCode(SC_OK) // Проверяем статус 200 (успешная авторизация)
                    .and()
                    .body("id", notNullValue()); // Проверяем, что успешный запрос возвращается непустой id-шник
        }

        // 2.  Проверка, что при отсутствии обязательного поля login попытка авторизации возвращает 400-сотую ошибку
        @DisplayName("400 Bad Request при отсутствии обязательного поля login")
        @Description("Нужно передавать обязательное поле login")
        @Test
        public void testMissingLoginError () {
            CourierLoginRequest requestBody = new CourierLoginRequest(null, uniquePassword);

            Response response =  given()
                    .baseUri(BASE_URL)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .when()
                    .post("/api/v1/courier/login");

            response.then()
                    .assertThat()
                    .statusCode(SC_BAD_REQUEST)                  // Проверяем статус 400(Bad Request)
                    .body(containsString("Недостаточно данных для входа"));
        }

        // 3.  Проверка, что при отсутствии обязательного поля password попытка авторизации 400-сотая ошибка
        // Но при выполнении получаем 504 (Gateway time out) - ошибка реализации, нужен дефект
        @DisplayName("400 Bad Request при отсутствии обязательного поля password")
        @Description("Нужно передавать обязательное поле password")
        @Test
        public void testMissingPasswordError () {

            CourierLoginRequest requestBody = new CourierLoginRequest(uniqueLogin, null);

            Response response =  given()
                    .baseUri(BASE_URL)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .when()
                    .post("/api/v1/courier/login");

            response.then()
                    .assertThat()
                    .statusCode(SC_BAD_REQUEST)                  // Ожидаем статус 400(Bad Request), а получаем 504 (Gateway time out)
                    .body(containsString("Недостаточно данных для входа"));
        }

        // 4.  Проверка, что при НЕверном login, но верном password попытка авторизации возвращает 404 ошибку
        @DisplayName("404 Not Found при неверном login, но верном password")
        @Description("Проверка соответствия login и password")
        @Test
        public void testInvalidLoginError () {
            CourierLoginRequest requestBody = new CourierLoginRequest("qwer", uniquePassword);

            Response response =  given()
                    .baseUri(BASE_URL)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .when()
                    .post("/api/v1/courier/login");

            response.then()
                    .assertThat()
                    .statusCode(SC_NOT_FOUND) // Проверяем статус 404 (Not Found)
                    .body(containsString("Учетная запись не найдена"));
        }

        // 5.  Проверка, что при НЕверном password, но верном login попытка авторизации возвращает 404 ошибку
        @DisplayName("404 Not Found при неверном password, но верном login")
        @Description("Проверка соответствия login и password")
        @Test
        public void testInvalidPasswordError () {
            CourierLoginRequest requestBody = new CourierLoginRequest(uniqueLogin, "qwer");

            Response response =  given()
                    .baseUri(BASE_URL)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .when()
                    .post("/api/v1/courier/login");

            response.then()
                    .assertThat()
                    .statusCode(SC_NOT_FOUND) // Проверяем статус 404 (Not Found)
                    .body(containsString("Учетная запись не найдена"));
        }

        // 6.  Проверка, что при НЕсуществующем login попытка авторизации возвращает 404 ошибку
        @DisplayName("404 Not Found при НЕсуществующем login")
        @Description("Проверка наличия login в БД")
        @Test
        public void testNonExisteLoginError () {
            deleteCourierById(courierId); // Удаляем курьера
            CourierLoginRequest requestBody = new CourierLoginRequest("111", uniquePassword);

            //Делаем попытку авторизоваться с данными удаленного курьера
            Response response =  given()
                    .baseUri(BASE_URL)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .when()
                    .post("/api/v1/courier/login");

            response.then()
                    .assertThat()
                    .statusCode(SC_NOT_FOUND) // Проверяем статус 404 (Not Found)
                    .body(containsString("Учетная запись не найдена"));
        }
    }

