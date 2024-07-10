package edu.practikum.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class BaseScenario {

    private static final ConnectionProperty CONNECTION_PROPERTIES = PropertyLoader.loadProperties();
    private final static String INSUFFICIENT_DATA_MESSAGE = "Недостаточно данных для входа";
    protected static RequestSpecification requestSpecification;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setUp() {

        /*
        Удаление тестовых данных созданных в процессе теста должно быть тут
        Именно перед запуском, чтобы после прогона теста, была возможность посмотреть
        на чем именно тест упал и "потрогать руками".

        Не стал делать удаление из-за отсутствия доступа к БД. Лучшим решением на мой взгляд
        будет генерировать сущности по шаблону auto_*** чтобы легко находить данные именно от
        автотестов базе и удалять

        Использовать API для удаления не стал по причине того, что:
        1. методы сами могут не работать,
        2. в тестах появляется необходимость запоминать как минимум ID созданной сущности,
        что нагружает тест и делает его менее читаемым
        3. Не удалять, а только скрывать данные на выходе (новые требования с 2018-19 годов)
        из баз ничего не удаляется безвозвратно, только сокрытие. И в таком случае при разрастании базы,
        она начинает очень сильно тормозить все приложение
        4. На удаление сущностей должны быть отдельные тесты
        5. Подобный флоу нужен для e2e тестах, которые тут пока не нужны.
         */

        requestSpecification = RestAssured
                .given()
                .baseUri(CONNECTION_PROPERTIES.getHost())
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .filter(new AllureRestAssuredFilter())
                .log().all();

        RestAssured.requestSpecification = requestSpecification;


    }


    @AfterAll
    public static void tearDown() {
        RestAssured.reset();
    }

    @Step(value = "{0}")
    public Response sendPostRequest(String promt, Object obj, String urlPath) {
        return given()
                .body(obj)
                .when()
                .post(urlPath);
    }

    @Step(value = "{0}")
    public Response sendEasyGetRequest(String promt, String urlPath, HashMap<String, String> params) {
        return given()
                .params(params)
                .when()
                .get(urlPath);
    }

    @Step(value = "{0}")
    public Response sendEasyGetRequest(String promt, String urlPath) {
        return given()
                .when()
                .get(urlPath);
    }

    public void logResponseToAllure(Response response) {
        //Добавил вручную, т.к. автоматически это почему то не срабатывает. Думаю из за ассерта в цепочке
        Allure.addAttachment("Response Status Code", String.valueOf(response.getStatusCode()));
        Allure.addAttachment("Response Headers", response.getHeaders().toString());
        Allure.addAttachment("Response Body", response.getBody().asPrettyString());
    }
}
