package courier.test;

import com.github.javafaker.Faker;
import courier.step.CourierStep;
import edu.practikum.dto.Courier;
import edu.practikum.util.BaseScenario;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;


@Slf4j
@DisplayName("Создание курьера")
public class CreateCourierTest extends BaseScenario {

    private final static String API_PATH = "api/v1/courier";
    private final static String FAIL_MESSAGE = "Недостаточно данных для создания учетной записи";
    private final static String CONFLICT_MESSAGE = "Этот логин уже используется. Попробуйте другой.";

    Faker faker = new Faker(new Locale("ru_Ru", "RU"));

    private CourierStep courierStep;

    @BeforeEach
    public void init() {
        courierStep = new CourierStep();
    }

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

        Response response = sendPostRequest("Вызов метода создания курьера", courier, API_PATH);
        courierStep
                .checkEasyResponse(response, SC_CREATED, "ok",
                        true, "Проверка положительного ответа");

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

        Response response = sendPostRequest("Вызов метода создания курьера", courier, API_PATH);
        courierStep
                .checkEasyResponse(response, SC_CREATED, "ok",
                        true, "Проверка положительного ответа");
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

        Response response = sendPostRequest("Вызов метода создания курьера", courier, API_PATH);
        courierStep
                .checkEasyResponse(response, SC_BAD_REQUEST, "message",
                        FAIL_MESSAGE, "Проверка негативного ответа");

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

        Response response = sendPostRequest("Вызов метода создания курьера", courier, API_PATH);
        courierStep
                .checkEasyResponse(response, SC_BAD_REQUEST, "message",
                        FAIL_MESSAGE, "Проверка негативного ответа");

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

        sendPostRequest("Вызов метода создания курьера", courier, API_PATH);
        Response response = sendPostRequest("Вызов метода создания курьера", courier, API_PATH);
        courierStep
                .checkEasyResponse(response, SC_CONFLICT, "message",
                        CONFLICT_MESSAGE, "Проверка ответа при создании дубля");

    }
}
