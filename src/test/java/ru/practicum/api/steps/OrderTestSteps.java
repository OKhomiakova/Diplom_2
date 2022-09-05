package ru.practicum.api.steps;

import POJO.Order;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.practicum.api.SetUp;

import static io.restassured.RestAssured.given;

public class OrderTestSteps {
    static String endPointOrders= "/api/orders";

    @Step("Создание нового заказа")
    public static Response createNewOrder(Order order, String accessToken) {
        Response response = given()
                .spec(SetUp.requestSpec())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(endPointOrders);
        return response;
    }

    @Step("Получение списка заказов")
    public static Response getOrderList(String accessToken) {
        Response response = given()
                .spec(SetUp.requestSpec())
                .header("authorization", accessToken)
                .when()
                .get(endPointOrders);
        return response;
    }
}
