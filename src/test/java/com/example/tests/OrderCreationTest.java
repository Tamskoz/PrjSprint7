package com.example.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.CreateCourierRequest;
import org.example.СreateOrderRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.example.CourierApi.createCourier;
import static org.example.OrderApi.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class OrderCreationTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private List<String> color;

    // Статический метод для генерации наборов входных данных
    static Stream<Arguments> provideColors() {
        return Stream.of(
                Arguments.of(Arrays.asList("BLACK")),      // Один цвет BLACK
                Arguments.of(Arrays.asList("GREY")),      // Один цвет GREY
                Arguments.of(Arrays.asList("BLACK", "GREY")), // Два цвета BLACK и GREY
                Arguments.of(List.of())                     // Без цвета
        );
    }

    // Параметризованный тест для разных вариантов передачи цветов
    @ParameterizedTest(name = "Создание заказа с цветами: {arguments}")
    @MethodSource("provideColors")
    public void orderCreationWithDifferentColors(List<String> colors) {
      // Формируем тело запроса с передачей списка цветов
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Татьяна");
        body.put("lastName", "Козлова");
        body.put("address", "Москва, Красная площадь, д.1");
        body.put("metroStation", "Краснопресненская");
        body.put("phone", "+7 900 009 09 09");
        body.put("rentTime", 14);
        body.put("deliveryDate", "2025-06-15T12:00:00");
        body.put("comment", "Будьте осторожны!!!");

        // Динамически добавляем список цветов в тело запроса
        if (!colors.isEmpty()) {
            body.put("color", colors);
        }

        // Создаем заказ
        СreateOrderRequest requestBody = new  СreateOrderRequest("Татьяна", "Козлова", "Москва, Красная площадь, д.1", "Краснопресненская", "+7 900 009 09 09", 14, "2025-06-15T12:00:00", "Будьте осторожны!!!", colors);
        Response response = createOrder(requestBody);

        response.then().log().all();  // Выводим полную информацию обо всех этапах запроса и ответа
        response.then()
                .assertThat()
                .statusCode(SC_CREATED)                   // Проверяем статус 201 (Created)
                .body(containsString("track")); // Проверяем наличие "track" в теле успешного ответа

        int strOrd = getOrderIdByTrack(response.jsonPath().getInt("track")); // Получаем id заказа по номеру трека
        // System.out.println(strOrd);
        cancelOrder(strOrd); //Отменяем заказ по id
    }
}

