package com.example.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.CourierApi;
import org.example.CourierLoginRequest;
import org.example.CreateCourierRequest;
import org.junit.jupiter.api.*;
import io.qameta.allure.Description;

import static org.apache.http.HttpStatus.*;
import static io.restassured.RestAssured.given;
import static org.example.CourierApi.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateCourierTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private int courierId; // Поле для хранения идентификатора курьера
    private String uniqueLogin = "";
    private String uniquePassword = "";
    private String uniqueFirstName = "";

    // Перед каждым тестом формируем уникальные значения login, password и firstName
    @BeforeEach
    void setUp() {
        uniqueLogin = "TamS" + System.currentTimeMillis();
        uniquePassword = "Pass" + System.currentTimeMillis();
        uniqueFirstName = "FirstName" + System.currentTimeMillis();
    }

    // Удаляем созданного курьера после каждого теста
    @AfterEach
    void tearDown() {
        if (!uniqueLogin.isEmpty() && uniquePassword != null) {
            int courierId = getCourierId(uniqueLogin, uniquePassword);
            if (courierId > 0) {
                deleteCourierById(courierId);
            }
        }
            else {
                System.out.println("Курьер с таким именем не найден, удаление пропущено.");
            }
    }

    // 1. Тест на успешное создание курьера с уникальными правильными параметрами 201 Created
    @DisplayName("201 Created при успешном создании курьера") //
    @Description("Успешое создание курьера с уникальными значениями атрибутов login, password и firstName")
    @Test
    public void createCourierSuccessfully() {

        // Создаем курьера с уникальными данными
        CreateCourierRequest requestBody = new CreateCourierRequest(uniqueLogin, uniquePassword, uniquePassword);

        Response response = createCourier(requestBody);
        response.then().log().all();  // Выводим полную информацию обо всех этапах запроса и ответа
        response.then().assertThat()
                .statusCode(SC_CREATED)               // Проверяем статус 201 (успешное создание)
                .body("ok", equalTo(true));    // Проверяем, что успешный запрос возвращает ok: true;
    }

    // 2. Повторное создание курьера с теми же данными возвращает ошибку 409 Conflict
    // Этот тест так же проверяет, что при создании курьера с таким же логином получем 409 ошибку
    @DisplayName("409 Conflict при попытке создать двух одинаковых курьеров")
    @Description("Нельзя создать двух курьеров с одними и теми же данными")
    @Test
    public void cannotCreateSameCourier() {

        // Создаем первый раз курьера с уникальными данными
        CreateCourierRequest initialRequestBody = new CreateCourierRequest(uniqueLogin, uniquePassword, uniquePassword);

        Response response = createCourier(initialRequestBody);
        response.then().log().all();  // Выводим полную информацию обо всех этапах запроса и ответа
        response.then().assertThat()
                .statusCode(SC_CREATED)               // Проверяем статус 201 (успешное создание)
                .body("ok", equalTo(true));    // Проверяем, что успешный запрос возвращает ok: true;

        // Повторная попытка создать курьера с такими же данными должна вернуть ошибку
        CreateCourierRequest duplicateRequestBody = new CreateCourierRequest(uniqueLogin, uniquePassword, uniquePassword);
        Response secondResponse = createCourier(duplicateRequestBody);
        secondResponse.then().log().all();  // Выводим полную информацию обо всех этапах запроса и ответа
        secondResponse.then().assertThat()
                .statusCode(SC_CONFLICT)
                .body(containsString("Этот логин уже используется. Попробуйте другой."));// Проверяем статус 409 (Conflict) */
    }

    // 3. Проверка, что при отсутствии обязательного поля password возвращается 400-сотая ошибка
    @DisplayName("400 Bad Request при отсутствии обязательного поля password")
    @Description("Нужно передавать обязательное поле password")
    @Test
    public void missingPasswordReturnsError() {

        // Создаем курьера с уникальными данными, но без указания пароля
        CreateCourierRequest invalidRequest = new CreateCourierRequest(uniqueLogin, null, uniquePassword);

        Response response = createCourier(invalidRequest);
        response.then().log().all();  // Выводим полную информацию обо всех этапах запроса и ответа
        response.then().assertThat()
                .statusCode(SC_BAD_REQUEST)                  // Проверяем статус 400 (Bad Request)
                .body(containsString("Недостаточно данных для создания учетной записи"));
    }

    // 4. Проверка, что при отсутствии обязательного поля login возвращается 400-сотая ошибка
    @DisplayName("400 Bad Request при отсутствии обязательного поля login")
    @Description("Нужно передавать обязательное поле login")
    @Test
    public void missingLoginReturnsError() {

        // Создаем курьера с уникальными данными, но без указания логина
        CreateCourierRequest invalidRequest = new CreateCourierRequest(null,uniquePassword, uniqueFirstName);

        Response response = createCourier(invalidRequest);
        response.then().log().all();  // Выводим полную информацию обо всех этапах запроса и ответа
        response.then().assertThat()
                .statusCode(SC_BAD_REQUEST)                  // Проверяем статус 400 (Bad Request)
                .body(containsString("Недостаточно данных для создания учетной записи"));
    }

    // 5. Проверка, что при отсутствии НЕобязательного поля firstName курьер успешно создается
    @DisplayName("201 Created при отсутствии НЕобязательного поля firstName")
    @Description("Успешное создание курьера без НЕобязательного поля firstName")
    @Test
    public void createCourierNoFirstNameSuccessfully() {

        // Создаем курьера с уникальными данными, нобез указания firstName
        CreateCourierRequest requestBody = new CreateCourierRequest(uniqueLogin, uniquePassword, "");

        Response response = createCourier(requestBody);
        response.then().log().all();  // Выводим полную информацию обо всех этапах запроса и ответа
        response.then().assertThat()
                .statusCode(SC_CREATED)               // Проверяем статус 201 (успешное создание)
                .body("ok", equalTo(true));    // Проверяем, что успешный запрос возвращает ok: true;
    }
}


