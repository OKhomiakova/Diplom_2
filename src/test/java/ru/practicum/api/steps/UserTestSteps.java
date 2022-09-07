package ru.practicum.api.steps;

import POJO.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.practicum.api.SetUp;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;

public class UserTestSteps {
    private final String endPointCreateUser = "/api/auth/register";
    private final String endPointLoginUser = "/api/auth/login";
    private final String endPointUpdateOrDeleteUser = "/api/auth/user";

    @Step("Создание нового пользователя")
    public Response createNewUser(User user) {
        Response response = given()
                .spec(SetUp.requestSpec())
                .body(user)
                .when()
                .post(endPointCreateUser);
        return response;
    }

    @Step("Логин пользователя (возвращает только accessToken)")
    public String loginUserReturnAccessToken(User credentials) {
        return given()
                .spec(SetUp.requestSpec())
                .body(credentials)
                .when()
                .post(endPointLoginUser).then().extract().path("accessToken");
    }

    @Step("Логин пользователя (возвращает полный response)")
    public Response loginUser(User credentials) {
        Response response = given()
                .spec(SetUp.requestSpec())
                .body(credentials)
                .when()
                .post(endPointLoginUser);
        return response;
    }

    @Step("Изменение данных пользователя")
    public Response updateUser(User user, String accessToken) {
        Response response = given()
                .spec(SetUp.requestSpec())
                .header("authorization", accessToken)
                .body(user)
                .when()
                .patch(endPointUpdateOrDeleteUser);
        return response;
    }

    @Step("Удалить пользователя")
    public Response deleteUser(String accessToken) {
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
