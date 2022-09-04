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

import java.util.List;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class GetOrderListTest {
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
    @DisplayName("Получение списка заказов (авторизованный пользователь)")
    public void getOrderListForAuthorizedUserTest() {
        Order order = new Order();
        OrderTestSteps.createNewOrder(order.getIngredientsForOrder(), accessToken);

        Response response = OrderTestSteps.getOrderList(accessToken);

        List<Object> orders = response.jsonPath().getList("orders");
        boolean isResponseSuccessful = response.jsonPath().getBoolean("success");

        assertFalse(orders.isEmpty());
        assertTrue(isResponseSuccessful);

    }

    @Test
    @DisplayName("Получение списка заказов неавторизованным пользователем")
    public void getOrderListForUnauthorizedUserTest() {
        Response response = OrderTestSteps.getOrderList("");

        int actualStatusCode = response.getStatusCode();
        String responseMessage = response.jsonPath().getString("message");

        assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        assertThat(responseMessage, equalTo("You should be authorised"));

    }
}
