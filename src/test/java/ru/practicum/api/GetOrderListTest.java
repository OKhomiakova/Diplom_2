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

import java.util.List;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class GetOrderListTest {
    private UserTestSteps userTestSteps;
    private OrderTestSteps orderTestSteps;
    private User user;
    private String accessToken;

    @Before
    public void generateDataForNewUser() {
        user = User.generateRandomUser();
        userTestSteps = new UserTestSteps();
        orderTestSteps = new OrderTestSteps();
        userTestSteps.createNewUser(this.user);
        accessToken = userTestSteps.loginUserReturnAccessToken(user);
    }

    @After
    public void deleteUser() {
        if (!(accessToken == null)) {
            userTestSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение списка заказов (авторизованный пользователь)")
    public void getOrderListForAuthorizedUserTest() {
        Order order = new Order();
        orderTestSteps.createNewOrder(order.getIngredientsForOrder(), accessToken);

        Response response = orderTestSteps.getOrderList(accessToken);

        List<Object> orders = response.jsonPath().getList("orders");
        boolean isResponseSuccessful = response.jsonPath().getBoolean("success");

        assertFalse(orders.isEmpty());
        assertTrue(isResponseSuccessful);

    }

    @Test
    @DisplayName("Получение списка заказов неавторизованным пользователем")
    public void getOrderListForUnauthorizedUserTest() {
        Response response = orderTestSteps.getOrderList("");

        int actualStatusCode = response.getStatusCode();
        String responseMessage = response.jsonPath().getString("message");

        assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        assertThat(responseMessage, equalTo("You should be authorised"));
    }
}
