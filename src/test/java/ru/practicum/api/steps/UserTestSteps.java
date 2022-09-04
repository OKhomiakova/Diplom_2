package ru.practicum.api.steps;

import POJO.User;
import POJO.UserCreds;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.practicum.api.SetUp;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;

public class UserTestSteps {
    static String endPointCreateUser = "/api/auth/register";
    static String endPointLoginUser = "/api/auth/login";
    static String endPointUpdateOrDeleteUser = "/api/auth/user";

    @Step("Создание нового пользователя")
    public static Response createNewUser(User user) {
        Response response = given()
                .spec(SetUp.requestSpec())
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(endPointCreateUser);
        return response;
    }

    @Step("Логин пользователя")
    public static Response loginUser(UserCreds credentials) {
        Response response = given()
                .spec(SetUp.requestSpec())
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post(endPointLoginUser);
        return response;
    }

    @Step("Изменение данных пользователя")
    public static Response updateUser(User user, String accessToken) {
        Response response = given()
                .spec(SetUp.requestSpec())
                .header("authorization", accessToken)
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(endPointUpdateOrDeleteUser);
        return response;
    }

    @Step("Удалить пользователя")
    public static Response deleteUser(String accessToken) {
        Response response = given()
                .spec(SetUp.requestSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(endPointUpdateOrDeleteUser)
                .then()
                .assertThat()
                .statusCode(SC_ACCEPTED)
                .extract()
                .path("ok");
        return response;
    }


}
