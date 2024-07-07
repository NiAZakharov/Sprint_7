package Courier;

import com.github.javafaker.Faker;
import dto.Courier;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.BaseScenario;

import java.util.Locale;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.equalTo;


@Slf4j
@DisplayName("Создание курьера")
public class CreateCourierTest extends BaseScenario {

    private final static String API_PATH = "api/v1/courier";
    private final static String FAIL_MESSAGE = "Недостаточно данных для создания учетной записи";
    private final static String CONFLICT_MESSAGE = "Этот логин уже используется. Попробуйте другой.";

    Faker faker = new Faker(new Locale("ru_Ru", "RU"));

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Создание курьера и проверка ответа метода")
    public void createSuccessfulCourierTest() {

        Courier courier = Courier
                .builder()
                .login(faker.name().username())
                .firstName(faker.name().firstName())
                .password(faker.name().username())
                .build();

        Response response = sendPostRequest("Вызов метода создания курьера",courier, API_PATH);
        checkSuccessfulResponse(response);
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Создание курьера без имени")
    public void createWithoutNameCourierTest() {

        Courier courier = Courier
                .builder()
                .login(faker.name().username())
                .password(faker.name().username())
                .build();

        Response response = sendPostRequest("Вызов метода создания курьера",courier, API_PATH);
        checkSuccessfulResponse(response);
    }

    @Test
    @DisplayName("Не успешное создание курьера")
    @Description("Создание курьера без пароля")
    public void createWithoutPasswordCourierTest() {

        Courier courier = Courier
                .builder()
                .login(faker.name().username())
                .firstName(faker.name().firstName())
                .build();

        Response response = sendPostRequest("Вызов метода создания курьера",courier, API_PATH);
        checkFailedResponse(response);
    }

    @Test
    @DisplayName("Не успешное создание курьера")
    @Description("Создание курьера без логина")
    public void createWithoutLoginCourierTest() {

        Courier courier = Courier
                .builder()
                .firstName(faker.name().firstName())
                .password(faker.name().username())
                .build();

        Response response = sendPostRequest("Вызов метода создания курьера",courier, API_PATH);
        checkFailedResponse(response);
    }

    @Test
    @DisplayName("Попытка создать существующего курьера")
    @Description("Попытка создать существующего курьера")
    public void createDopplerCourierTest() {

        Courier courier = Courier
                .builder()
                .login(faker.name().username())
                .firstName(faker.name().firstName())
                .password(faker.name().username())
                .build();

        sendPostRequest("Вызов метода создания курьера",courier, API_PATH);
        Response response = sendPostRequest("Вызов метода создания курьера",courier, API_PATH);
        checkDopplerResponse(response);
    }

    @Step(value = "Проверка положительного ответа")
    public void checkSuccessfulResponse(Response response) {
        log.info(response.prettyPrint());
        logResponseToAllure(response);
        response.then()
                .assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(SC_CREATED);
    }

    @Step(value = "Проверка негативного ответа")
    public void checkFailedResponse(Response response) {
        log.info(response.prettyPrint());
        logResponseToAllure(response);
        response.then()
                .assertThat().body("message", equalTo(FAIL_MESSAGE))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Step(value = "Проверка ответа при создании дубля")
    public void checkDopplerResponse(Response response) {
        log.info(response.prettyPrint());
        logResponseToAllure(response);
        response.then()
                .assertThat().body("message", equalTo(CONFLICT_MESSAGE))
                .and()
                .statusCode(SC_CONFLICT);
    }
}
