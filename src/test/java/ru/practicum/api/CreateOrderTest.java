package ru.practicum.api;

import POJO.Order;
import POJO.User;
import POJO.UserCreds;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
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
    private User user;
    private String accessToken;

    @Before
    public void generateDataForNewUser() {
        String email = RandomStringUtils.randomAlphanumeric(5) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphanumeric(6);
        String name = RandomStringUtils.randomAlphanumeric(6);

        this.user = new User(email, password, name);

        UserTestSteps.createNewUser(this.user);
        Response responseLogin = UserTestSteps.loginUser(UserCreds.from(this.user));
        accessToken = responseLogin.jsonPath().getString("accessToken");
    }

    @After
    public void deleteUser() {
        if (!(accessToken == null)) {
            UserTestSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание нового заказа (с авторизацией)")
    public void orderCreateWithAuthorizationTest() {
        Order order = new Order();
        Response orderCreate = OrderTestSteps.createNewOrder(order.getIngredientsForOrder(), accessToken);

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
        Response orderCreate = OrderTestSteps.createNewOrder(order.getIngredientsForOrder(), "");

        int actualStatusCode = orderCreate.getStatusCode();
        boolean isResponseSuccessful = orderCreate.jsonPath().getBoolean("success");

        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isResponseSuccessful);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void orderCreateOutIngredientTest() {
        Order order = new Order(null);
        Response orderCreate = OrderTestSteps.createNewOrder(order, accessToken);

        int actualStatusCode = orderCreate.getStatusCode();
        String responseMessage = orderCreate.jsonPath().getString("message");

        assertEquals(SC_BAD_REQUEST, actualStatusCode);
        assertThat(responseMessage, equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с невалидным хешем ингредиентов")
    public void orderCreateWithInvalidIngredientTest() {
        Order order = new Order();
        Response orderCreate = OrderTestSteps.createNewOrder(order.getInvalidIngredientsForOrder(), accessToken);

        int actualStatusCode = orderCreate.getStatusCode();
        assertEquals(SC_INTERNAL_SERVER_ERROR, actualStatusCode);
    }
}
