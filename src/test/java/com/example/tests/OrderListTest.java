package com.example.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;
import static org.apache.http.HttpStatus.*;
import static io.restassured.RestAssured.given;
import static org.example.OrderApi.*;
import static org.example.CourierApi.*;
import static org.example.OrderApi.createOrderAndGetTrack;
import static org.hamcrest.Matchers.*;

public class OrderListTest {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    @DisplayName("200 OK при наличии в теле ответа список заказов.")
    @Description("Проверка, что для курьера возвращается список заказов")
    @Test
    public void testOrderList() {

        // Формируем уникальные значения login, password и firstName для каждого запуска
        String uniqueLogin = "TamS" + System.currentTimeMillis();
        String uniquePassword = "Pass" + System.currentTimeMillis();
        String uniqueFirstName = "FirstName" + System.currentTimeMillis();

        createCourier(uniqueLogin,uniquePassword,uniqueFirstName);   //Создаем курьера с этими уникальными значениями
        int idC = getCourierId(uniqueLogin,uniquePassword); // Прихраниваем id курьера
        // System.out.println(idC);
        int trackIdOrder = createOrderAndGetTrack(); // Создаем заказ и прихраниваем его traсk
        // System.out.println(trackIdOrder);
        int IdOrder = getOrderIdByTrack(trackIdOrder); //  Получаем id заказа по его traсk и прихраниваем
        // System.out.println(IdOrder);
        acceptOrder(IdOrder, idC); // Для созданного курьера принимаем заказ по id

        // Запускаем вызов запроса на получение списка заказов курьера
        Response response = given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .get("/api/v1/orders?courierId="+idC);

        response.then()
                .assertThat()
                .statusCode(SC_OK); // Проверяем успешный статус 200 ОК */

        // Проверка наличия массива orders в теле ответа
        response.then()
                .body("orders", hasSize(greaterThanOrEqualTo(1))); // Массив заказов не пуст

        deleteCourierById(idC);  // Удаляем созданного курьера по id
        cancelOrder(IdOrder); //Отменяем заказ по id
    }
}
