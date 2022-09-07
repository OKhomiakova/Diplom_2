package ru.practicum.api;

import POJO.User;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.api.steps.UserTestSteps;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoginUserTest {
    private UserTestSteps userTestSteps;
    private User user;
    private String accessToken;

    @Before
    public void generateDataForNewUser() {
        user = User.generateRandomUser();
        userTestSteps = new UserTestSteps();
        userTestSteps.createNewUser(user);
    }

    @After
    public void deleteUser() {
        User credentials = new User(user.getEmail(), user.getPassword(), null);
        accessToken = userTestSteps.loginUserReturnAccessToken(credentials);
        if (!(accessToken == null)) {
            userTestSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void loginUserSuccess() {;
         Response responseLogin = userTestSteps.loginUser(user);

//        System.out.println(user.getEmail());
//        System.out.println(user.getPassword());
//        System.out.println(UserCreds.from(this.user).getEmail());
//        System.out.println(UserCreds.from(this.user).getPassword());
//        responseLogin.prettyPrint();

        int actualStatusCode = responseLogin.getStatusCode();
        boolean isResponseSuccessful = responseLogin.jsonPath().getBoolean("success");
        String email = responseLogin.jsonPath().getString("user.email");
        String name = responseLogin.jsonPath().getString("user.name");
        accessToken = responseLogin.body().jsonPath().getString("accessToken");
        String refreshToken = responseLogin.body().jsonPath().getString("refreshToken");

        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isResponseSuccessful);
        assertEquals(user.getEmail().toLowerCase(), email);
        assertEquals(user.getName(), name);
        assertThat(accessToken, notNullValue());
        assertThat(refreshToken, notNullValue());
    }

    @Test
    @DisplayName("логин с неверным логином и паролем")
    public void loginUserWithInvalidCredentialsTest() {
        User invalidCreds = new User(RandomStringUtils.randomAlphabetic(5) + "@yandex.ru", RandomStringUtils.randomNumeric(6), user.getName());

        Response responseLogin = userTestSteps.loginUser(invalidCreds);

        int statusCode = responseLogin.getStatusCode();
        String responseMessage = responseLogin.body().jsonPath().getString("message");
        accessToken = responseLogin.body().jsonPath().getString("accessToken");

        assertThat(statusCode, equalTo(401));
        assertThat(responseMessage, equalTo("email or password are incorrect"));
    }

}
