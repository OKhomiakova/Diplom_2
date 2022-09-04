package ru.practicum.api;

import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class SetUp {

    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";

    public static RequestSpecification requestSpec() {
        return given()
                .baseUri(BASE_URL);
        }
}
