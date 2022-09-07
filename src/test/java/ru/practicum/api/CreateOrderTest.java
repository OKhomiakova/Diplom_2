package ru.practicum.api;

import POJO.Order;
import POJO.User;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.api.steps.OrderTestSteps;
import ru.practicum.api.steps.UserTestSteps;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateOrderTest {
    private static UserTestSteps userTestSteps;
    private static OrderTestSteps orderTestSteps;
    private static User user;
    private String accessToken;

    @Before
    public void generateDataForNewUser() {
        user = User.generateRandomUser();
        userTestSteps = new UserTestSteps();
        orderTestSteps = new OrderTestSteps();
        userTestSteps.createNewUser(user);
        accessToken = userTestSteps.loginUserReturnAccessToken(user);
    }

    @After
    public void deleteUser() {
        if (!(accessToken == null)) {
            userTestSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание нового заказа (с авторизацией)")
    public void orderCreateWithAuthorizationTest() {
        Order order = new Order();
        Response orderCreate = orderTestSteps.createNewOrder(order.getIngredientsForOrder(), accessToken);

        //orderCreate.prettyPrint();

        int actualStatusCode = orderCreate.getStatusCode();
        boolean isResponseSuccessful = orderCreate.jsonPath().getBoolean("success");

        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isResponseSuccessful);
    }

    @Test
    @DisplayName("Создание нового заказа (без авторизации)")
    public void orderCreateOutAuthorizationTest() {
        Order order = new Order();
        Response orderCreate = orderTestSteps.createNewOrder(order.getIngredientsForOrder(), "");

        int actualStatusCode = orderCreate.getStatusCode();
        boolean isResponseSuccessful = orderCreate.jsonPath().getBoolean("success");

        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isResponseSuccessful);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void orderCreateOutIngredientTest() {
        Order order = new Order(null);
        Response orderCreate = orderTestSteps.createNewOrder(order, accessToken);

        int actualStatusCode = orderCreate.getStatusCode();
        String responseMessage = orderCreate.jsonPath().getString("message");

        assertEquals(SC_BAD_REQUEST, actualStatusCode);
        assertThat(responseMessage, equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с невалидным хешем ингредиентов")
    public void orderCreateWithInvalidIngredientTest() {
        Order order = new Order();
        Response orderCreate = orderTestSteps.createNewOrder(order.getInvalidIngredientsForOrder(), accessToken);

        int actualStatusCode = orderCreate.getStatusCode();
        assertEquals(SC_INTERNAL_SERVER_ERROR, actualStatusCode);
    }
}
