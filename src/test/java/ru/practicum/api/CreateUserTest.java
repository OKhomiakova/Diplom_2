package ru.practicum.api;

import POJO.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.api.steps.OrderTestSteps;
import ru.practicum.api.steps.UserTestSteps;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class CreateUserTest {
    private static UserTestSteps userTestSteps;
    private User user;
    private String accessToken;

    @Before
    public void generateDataForNewUser() {
        user = User.generateRandomUser();
        userTestSteps = new UserTestSteps();
    }

    @After
    public void deleteUser() {
        accessToken = userTestSteps.loginUserReturnAccessToken(user);
        if (!(accessToken == null)) {
            userTestSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создать уникального пользователя")
    @Description("Создание нового пользователя с полными, корректными данными")
    public void createNewUserWithCorrectAndSufficientData() {
        Response response = userTestSteps.createNewUser(user);

//      response.prettyPrint();

        int actualStatusCode = response.getStatusCode();
        boolean isResponseSuccessful = response.jsonPath().getBoolean("success");
        String email = response.jsonPath().getString("user.email");
        String name = response.jsonPath().getString("user.name");
        accessToken = response.body().jsonPath().getString("accessToken");
        String refreshToken = response.body().jsonPath().getString("refreshToken");

        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isResponseSuccessful);
        assertEquals(user.getEmail().toLowerCase(), email);
        assertEquals(user.getName(), name);
        assertThat(accessToken, notNullValue());
        assertThat(refreshToken, notNullValue());
    }

        @Test
        @DisplayName("Повторное создание существующего пользователя")
        public void createUserWithDuplicateData() {
            Response firstResponse = userTestSteps.createNewUser(user);
            Response secondResponse = userTestSteps.createNewUser(user);

            accessToken = firstResponse.body().jsonPath().getString("accessToken");

            int actualStatusCode = secondResponse.getStatusCode();
            boolean isResponseSuccessful = secondResponse.jsonPath().getBoolean("success");
            String responseMessage = secondResponse.jsonPath().getString("message");

            assertEquals(SC_FORBIDDEN, actualStatusCode);
            assertFalse(isResponseSuccessful);
            assertEquals("User already exists", responseMessage);
            assertThat(accessToken, notNullValue());
        }

    @Test
    @DisplayName("Создание пользователя с пустым email")
    public void createNewUserWithEmptyEmail() {
        User user = new User("", RandomStringUtils.randomAlphanumeric(6), RandomStringUtils.randomAlphanumeric(6));
        Response response = userTestSteps.createNewUser(user);

        int actualStatusCode = response.statusCode();
        boolean isResponseSuccessful = response.jsonPath().getBoolean("success");
        String responseMessage = response.jsonPath().getString("message");
        accessToken = response.body().jsonPath().getString("accessToken");

        assertEquals(SC_FORBIDDEN, actualStatusCode);
        assertFalse(isResponseSuccessful);
        assertEquals("Email, password and name are required fields", responseMessage);
    }

    @Test
    @DisplayName("Создание пользователя без поля email")
    public void createNewUserWithoutEmail() {
        User user = new User(null, RandomStringUtils.randomAlphanumeric(6), RandomStringUtils.randomAlphanumeric(6));
        Response response = userTestSteps.createNewUser(user);

        int actualStatusCode = response.statusCode();
        boolean isResponseSuccessful = response.jsonPath().getBoolean("success");
        String responseMessage = response.jsonPath().getString("message");
        accessToken = response.body().jsonPath().getString("accessToken");

        assertEquals(SC_FORBIDDEN, actualStatusCode);
        assertFalse(isResponseSuccessful);
        assertEquals("Email, password and name are required fields", responseMessage);
    }

    @Test
    @DisplayName("Создание пользователя с пустым password")
    public void createNewUserWithEmptyPassword() {
        User user = new User(RandomStringUtils.randomAlphanumeric(5) + "@yandex.ru", "", RandomStringUtils.randomAlphanumeric(6));
        Response response = userTestSteps.createNewUser(user);

        int actualStatusCode = response.statusCode();
        boolean isResponseSuccessful = response.jsonPath().getBoolean("success");
        String responseMessage = response.jsonPath().getString("message");
        accessToken = response.body().jsonPath().getString("accessToken");

        assertEquals(SC_FORBIDDEN, actualStatusCode);
        assertFalse(isResponseSuccessful);
        assertEquals("Email, password and name are required fields", responseMessage);
    }

    @Test
    @DisplayName("Создание пользователя без поля password")
    public void createNewUserWithoutPassword() {
        User user = new User(RandomStringUtils.randomAlphanumeric(5) + "@yandex.ru", null, RandomStringUtils.randomAlphanumeric(6));
        Response response = userTestSteps.createNewUser(user);

        int actualStatusCode = response.statusCode();
        boolean isResponseSuccessful = response.jsonPath().getBoolean("success");
        String responseMessage = response.jsonPath().getString("message");
        accessToken = response.body().jsonPath().getString("accessToken");

        assertEquals(SC_FORBIDDEN, actualStatusCode);
        assertFalse(isResponseSuccessful);
        assertEquals("Email, password and name are required fields", responseMessage);
    }

    @Test
    @DisplayName("Создание пользователя с пустым name")
    public void createNewUserWithEmptyName() {
        User user = new User(RandomStringUtils.randomAlphanumeric(5) + "@yandex.ru", RandomStringUtils.randomAlphanumeric(6), "");
        Response response = userTestSteps.createNewUser(user);

        int actualStatusCode = response.statusCode();
        boolean isResponseSuccessful = response.jsonPath().getBoolean("success");
        String responseMessage = response.jsonPath().getString("message");
        accessToken = response.body().jsonPath().getString("accessToken");

        assertEquals(SC_FORBIDDEN, actualStatusCode);
        assertFalse(isResponseSuccessful);
        assertEquals("Email, password and name are required fields", responseMessage);
    }

    @Test
    @DisplayName("Создание пользователя без поля name")
    public void createNewUserWithoutName() {
        User user = new User(RandomStringUtils.randomAlphanumeric(5) + "@yandex.ru", RandomStringUtils.randomAlphanumeric(6), null);
        Response response = userTestSteps.createNewUser(user);

        int actualStatusCode = response.statusCode();
        boolean isResponseSuccessful = response.jsonPath().getBoolean("success");
        String responseMessage = response.jsonPath().getString("message");
        accessToken = response.body().jsonPath().getString("accessToken");

        assertEquals(SC_FORBIDDEN, actualStatusCode);
        assertFalse(isResponseSuccessful);
        assertEquals("Email, password and name are required fields", responseMessage);
    }

}
