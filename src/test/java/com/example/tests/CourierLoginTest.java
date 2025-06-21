package com.example.tests;

import io.restassured.response.Response;
import org.example.CourierApi;
import org.example.CourierLoginRequest;
import org.example.CreateCourierRequest;
import org.junit.jupiter.api.*;
import io.qameta.allure.Description;

import static org.apache.http.HttpStatus.*;
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
        CreateCourierRequest requestBody = new CreateCourierRequest(uniqueLogin, uniquePassword, uniqueFirstName);
        createCourier(requestBody); //
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
        public void testSuccessfulLogin() {

            int returnedId = CourierApi.getCourierId(uniqueLogin, uniquePassword);
            // Проверяем, что возвращаемый ID не равен нулю (означает успешную авторизацию)
            Assertions.assertNotEquals(returnedId, 0,
                    "Авторизация прошла успешно, возвращён ID курьера.");
        }

        // 2.  Проверка, что при отсутствии обязательного поля login попытка авторизации возвращает 400-сотую ошибку
        @DisplayName("400 Bad Request при отсутствии обязательного поля login")
        @Description("Нужно передавать обязательное поле login")
        @Test
        public void testMissingLoginError() {

            CourierLoginRequest invalidRequest = new CourierLoginRequest(null, uniquePassword);
            Response response = LoginCourier(invalidRequest);
            response.then().assertThat()
                    .statusCode(equalTo(SC_BAD_REQUEST))
                    .log().ifValidationFails(); // Для вывода логов при провале теста

        }

        // 3.  Проверка, что при отсутствии обязательного поля password попытка авторизации 400-сотая ошибка
        // Но при выполнении получаем 504 (Gateway time out) - ошибка реализации, нужен дефект
        @DisplayName("400 Bad Request при отсутствии обязательного поля password")
        @Description("Нужно передавать обязательное поле password")
        @Test
        public void testMissingPasswordError () {

            CourierLoginRequest invalidRequest = new CourierLoginRequest(uniqueLogin, null);
            Response response = LoginCourier(invalidRequest);

            response.then().assertThat()
                    .statusCode(equalTo(SC_BAD_REQUEST))
                    .log().ifValidationFails(); // Для вывода логов при провале теста

        }

        // 4.  Проверка, что при НЕверном login, но верном password попытка авторизации возвращает 404 ошибку
        @DisplayName("404 Not Found при неверном login, но верном password")
        @Description("Проверка соответствия login и password")
        @Test
        public void testInvalidLoginError () {
            CourierLoginRequest invalidRequest = new CourierLoginRequest("qwer", uniquePassword);
            Response response = LoginCourier(invalidRequest);

            response.then().assertThat()
                    .statusCode(equalTo(SC_NOT_FOUND))
                    .log().ifValidationFails(); // Для вывода логов при провале теста
        }

        // 5.  Проверка, что при НЕверном password, но верном login попытка авторизации возвращает 404 ошибку
        @DisplayName("404 Not Found при неверном password, но верном login")
        @Description("Проверка соответствия login и password")
        @Test
        public void testInvalidPasswordError () {
            CourierLoginRequest invalidRequest = new CourierLoginRequest(uniqueLogin, "qwer");
            Response response = LoginCourier(invalidRequest);

            response.then().assertThat()
                    .statusCode(equalTo(SC_NOT_FOUND))
                    .log().ifValidationFails(); // Для вывода логов при провале теста
        }

        // 6.  Проверка, что при НЕсуществующем login попытка авторизации возвращает 404 ошибку
        @DisplayName("404 Not Found при НЕсуществующем login")
        @Description("Проверка наличия login в БД")
        @Test
        public void testNonExisteLoginError () {
            deleteCourierById(courierId); // Удаляем курьера
            CourierLoginRequest invalidRequest = new CourierLoginRequest("111", uniquePassword);
            Response response = LoginCourier(invalidRequest);

            response.then().assertThat()
                    .statusCode(equalTo(SC_NOT_FOUND))
                    .log().ifValidationFails(); // Для вывода логов при провале теста

        }
    }

