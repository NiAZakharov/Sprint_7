package Courier;

import com.github.javafaker.Faker;
import dto.Courier;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.BaseScenario;

import java.util.Locale;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@DisplayName("Логин курьера в системе")
public class LoginCourierTest extends BaseScenario {

    private final static String API_LOGIN_PATH = "api/v1/courier/login";
    private final static String API_CREATE_PATH = "api/v1/courier";
    private final static String NOT_FOUND_MESSAGE = "Учетная запись не найдена";
    private final static String INSUFFICIENT_DATA_MESSAGE = "Недостаточно данных для входа";

    Faker faker = new Faker(new Locale("ru_Ru", "RU"));


    @Test
    @DisplayName("Авторизация курьером")
    @Description("Успешная авторизация существующим курьером")
    public void courierCanLoginTest() {

        Courier courier = Courier
                .builder()
                .login(faker.name().username())
                .password(faker.name().username())
                .build();

        sendPostRequest("Создание курьера для авторизации", courier, API_CREATE_PATH);
        Response response = sendPostRequest("Авторизация", courier, API_LOGIN_PATH);
        checkSuccessResponse(response);
    }

    @Test
    @DisplayName("Попытка авторизоваться не зарегистрированным курьером")
    @Description("Попытка авторизоваться не зарегистрированным курьером")
    public void anonymousCourierTest() {
        Courier courier = Courier
                .builder()
                .login(faker.name().username())
                .password(faker.name().username())
                .build();

        Response response = sendPostRequest("Авторизация", courier, API_LOGIN_PATH);
        checkNotFoundResponse(response);
    }

    @Test
    @DisplayName("Авторизоваться без пароля")
    @Description("В качестве пароля передается ключ со значением NULL")
    public void courierLoginWithEmptyPassTest() {

        Courier courier = Courier
                .builder()
                .login(faker.name().username())
                .password(faker.name().username())
                .build();

        sendPostRequest("Создание курьера для авторизации", courier, API_CREATE_PATH);

        //Курьера создаем обычным способом, но при авторизации используется тот же объект
        //поэтому пароль принудительно затирается
        //Тут есть проблема. Собирался избавиться от передачи в body не инициализированных ключей
        //"id" : null, и добавил в класс курьера аннотацию. Теперь не получится передать ключ без value
        courier.setPassword(null);
        Response response = sendPostRequest("Авторизация", courier, API_LOGIN_PATH);
        checkErrorResponse(response);
    }

    @Test
    @DisplayName("Авторизоваться без пароля")
    @Description("В качестве пароля передается ключ со значением пустой строки")
    public void courierLoginWithoutPassTest() {

        Courier courier = Courier
                .builder()
                .login(faker.name().username())
                .password(faker.name().username())
                .build();

        sendPostRequest("Создание курьера для авторизации", courier, API_CREATE_PATH);

        //Курьера создаем обычным способом, но при авторизации используется тот же объект
        //поэтому пароль принудительно затирается
        courier.setPassword("");
        Response response = sendPostRequest("Авторизация", courier, API_LOGIN_PATH);
        checkErrorResponse(response);
    }

    @Test
    @DisplayName("Авторизоваться без пароля")
    @Description("В качестве пароля передается не верный пароль")
    public void courierLoginWithWrongPassTest() {

        Courier courier = Courier
                .builder()
                .login(faker.name().username())
                .password(faker.name().username())
                .build();

        sendPostRequest("Создание курьера для авторизации", courier, API_CREATE_PATH);

        courier.setPassword(faker.random().toString());
        Response response = sendPostRequest("Авторизация", courier, API_LOGIN_PATH);
        log.info(response.prettyPrint());
        response.then()
                .assertThat().body("message", equalTo(NOT_FOUND_MESSAGE))
                .and()
                .statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Авторизоваться без пароля")
    @Description("Ключ пароля не передается в тело метода")
    public void courierLoginWithoutKeyPassTest() {

        Courier courier = Courier
                .builder()
                .login(faker.name().username())
                .password(faker.name().username())
                .build();

        sendPostRequest("Создание курьера для авторизации", courier, API_CREATE_PATH);

        Courier courier2 = Courier
                .builder()
                .login(courier.getLogin())
                .build();

        Response response = sendPostRequest("Авторизация", courier2, API_LOGIN_PATH);
        checkErrorResponse(response);
    }

    @Test
    @DisplayName("Авторизоваться без логина")
    @Description("Логин не передается в тело метода")
    public void courierLoginWithoutLoginTest() {

        Courier courier = Courier
                .builder()
                .login(faker.name().username())
                .password(faker.name().username())
                .build();

        sendPostRequest("Создание курьера для авторизации", courier, API_CREATE_PATH);

        Courier courier2 = Courier
                .builder()
                .password(courier.getPassword())
                .build();

        Response response = sendPostRequest("Авторизация", courier2, API_LOGIN_PATH);
        checkErrorResponse(response);
    }

    @Step(value = "Проверка некорректного запроса 400")
    public void checkErrorResponse(Response response) {
        log.info(response.prettyPrint());
        logResponseToAllure(response);
        response.then()
                .assertThat().body("message", equalTo(INSUFFICIENT_DATA_MESSAGE))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Step(value = "Проверка некорректного запроса 404")
    public void checkNotFoundResponse(Response response) {
        log.info(response.prettyPrint());
        logResponseToAllure(response);
        response.then()
                .assertThat().body("message", equalTo(NOT_FOUND_MESSAGE))
                .and()
                .statusCode(SC_NOT_FOUND);
    }

    @Step(value = "Проверка успешного запроса")
    public void checkSuccessResponse(Response response) {
        log.info(response.prettyPrint());
        logResponseToAllure(response);
        response.then()
                .assertThat().body("id", Matchers.any(Integer.class))
                .and()
                .statusCode(SC_OK);
    }

}
