package ru.practicum.api;

import POJO.User;
import POJO.UserCreds;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.api.steps.UserTestSteps;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UpdateUserTest {
    private User user;
    private String accessToken;

    @Before
    public void generateDataForNewUser() {
        String email = RandomStringUtils.randomAlphanumeric(5) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphanumeric(6);
        String name = RandomStringUtils.randomAlphanumeric(6);

        this.user = new User(email, password, name);

        UserTestSteps.createNewUser(user);
        Response responseLogin = UserTestSteps.loginUser(UserCreds.from(user));
        accessToken = responseLogin.body().jsonPath().getString("accessToken");
    }

    @After
    public void deleteUser() {
        if (!(accessToken == null)) {
            UserTestSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Изменение данных пользователя (с авторизацией)")
    public void updateUserSuccess() {
        User updatedUser = new User(RandomStringUtils.randomAlphanumeric(5) + "@yandex.ru", RandomStringUtils.randomAlphanumeric(6), RandomStringUtils.randomAlphanumeric(6));

        Response responseUpdate = UserTestSteps.updateUser(updatedUser, accessToken);

        int actualStatusCode = responseUpdate.getStatusCode();
        boolean isResponseSuccessful = responseUpdate.jsonPath().getBoolean("success");
        String email = responseUpdate.jsonPath().getString("user.email");
        String name = responseUpdate.jsonPath().getString("user.name");

        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isResponseSuccessful);
        assertEquals(updatedUser.getEmail().toLowerCase(), email);
        assertEquals(updatedUser.getName(), name);

    }

    @Test
    @DisplayName("Обновление данных пользователя (без авторизации)")
    public void updateUserWithoutLogin() {
        User updatedUser = new User(RandomStringUtils.randomAlphanumeric(5) + "@yandex.ru", RandomStringUtils.randomAlphanumeric(6), RandomStringUtils.randomAlphanumeric(6));
        Response responseUpdate = UserTestSteps.updateUser(updatedUser, "");

        int actualStatusCode = responseUpdate.getStatusCode();
        String responseMessage = responseUpdate.jsonPath().getString("message");

        assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        assertEquals("You should be authorised", responseMessage);
    }

    @Test
    @DisplayName("Изменение только одного поля email (с авторизацией)")
    public void userUpdateEmailFieldTest() {
        String newEmail = RandomStringUtils.randomAlphanumeric(5) + "@yandex.ru";
        User updatedEmailUser = new User(newEmail, user.getPassword(), user.getName());
        Response responseUpdate = UserTestSteps.updateUser(updatedEmailUser, accessToken);

        int actualStatusCode = responseUpdate.getStatusCode();
        boolean isResponseSuccessful = responseUpdate.jsonPath().getBoolean("success");
        String email = responseUpdate.jsonPath().getString("user.email");
        String name = responseUpdate.jsonPath().getString("user.name");

        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isResponseSuccessful);
        assertEquals(updatedEmailUser.getEmail().toLowerCase(), email);
        assertEquals(updatedEmailUser.getName(), name);
    }

    @Test
    @DisplayName("Изменение только одного поля email (без авторизации)")
    public void userUpdateEmailFieldWithoutAuthorizationTest() {
        String newEmail = RandomStringUtils.randomAlphanumeric(5) + "@yandex.ru";
        User updatedEmailUser = new User(newEmail, user.getPassword(), user.getName());
        Response responseUpdate = UserTestSteps.updateUser(updatedEmailUser, "");

        int actualStatusCode = responseUpdate.getStatusCode();
        String responseMessage = responseUpdate.jsonPath().getString("message");

        assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        assertEquals("You should be authorised", responseMessage);
    }

    @Test
    @DisplayName("Изменение только одного поля email (без авторизации)")
    public void userUpdatePasswordFieldWithoutAuthorizationTest() {
        String newPassword = RandomStringUtils.randomAlphanumeric(6);
        User updatedPasswordUser = new User(user.getEmail(), newPassword, user.getName());
        Response responseUpdate = UserTestSteps.updateUser(updatedPasswordUser, "");

        int actualStatusCode = responseUpdate.getStatusCode();
        String responseMessage = responseUpdate.jsonPath().getString("message");

        assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        assertEquals("You should be authorised", responseMessage);
    }

    @Test
    @DisplayName("Изменение только одного поля name (с авторизацией)")
    public void userUpdateNameFieldTest() {
        String newName = RandomStringUtils.randomAlphanumeric(6);
        User updatedNameUser = new User(user.getEmail(), user.getPassword(), newName);
        Response responseUpdate = UserTestSteps.updateUser(updatedNameUser, accessToken);

        int actualStatusCode = responseUpdate.getStatusCode();
        boolean isResponseSuccessful = responseUpdate.jsonPath().getBoolean("success");
        String email = responseUpdate.jsonPath().getString("user.email");
        String name = responseUpdate.jsonPath().getString("user.name");

        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isResponseSuccessful);
        assertEquals(updatedNameUser.getEmail().toLowerCase(), email);
        assertEquals(updatedNameUser.getName(), name);
    }

    @Test
    @DisplayName("Изменение только одного поля name (без авторизации)")
    public void userUpdateNameFieldWithoutAuthorizationTest() {
        String newName = RandomStringUtils.randomAlphanumeric(6);
        User updatedNameUser = new User(user.getEmail(), user.getPassword(), newName);
        Response responseUpdate = UserTestSteps.updateUser(updatedNameUser, "");

        int actualStatusCode = responseUpdate.getStatusCode();
        String responseMessage = responseUpdate.jsonPath().getString("message");

        assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        assertEquals("You should be authorised", responseMessage);
    }

    @Test
    @DisplayName("Изменение поля email на уже занятую почту")
    public void userUpdateEmailFieldWithTakenEmailTest() {
        User newUser = new User(RandomStringUtils.randomAlphanumeric(5) + "@yandex.ru", RandomStringUtils.randomAlphanumeric(6), RandomStringUtils.randomAlphanumeric(6));
        UserTestSteps.createNewUser(newUser);

        Response responseLoginNewUser = UserTestSteps.loginUser(UserCreds.from(newUser));
        String emailNewUser = responseLoginNewUser.body().jsonPath().getString("user.email");

        User updateUserWithTakenEmail = new User(emailNewUser, user.getPassword(), user.getName());
        Response responseUpdateReg = UserTestSteps.updateUser(updateUserWithTakenEmail, accessToken);

        int actualStatusCode = responseUpdateReg.getStatusCode();
        String responseMessage = responseUpdateReg.jsonPath().getString("message");

        assertEquals(SC_FORBIDDEN, actualStatusCode);
        assertEquals("User with such email already exists", responseMessage);

    }
}
