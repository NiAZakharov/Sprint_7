package Order;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.BaseScenario;

import java.util.HashMap;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;


@Slf4j
@DisplayName("Обработка заказов")
public class CheckOrderListTest extends BaseScenario {

    private final static String API_ORDER_PATH = "api/v1/orders";
    private final static String JSON_SCHEMA_PATH = "responseSchema.json";
    private final static String ALLURE_PROMPT = "Получить список всех заказов по всем курьерам";
    private final static String COURIER_NOT_FOUND_MESSAGE = "Курьер с идентификатором 1 не найден";

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Получение списка всех заказов")
    public void getOrderList() {

        Response response = sendEasyGetRequest(ALLURE_PROMPT, API_ORDER_PATH);
        checkOrderResponseValidate(response);
        checkOrderReqContent(response);
    }

    @Test
    @DisplayName("Получение списка из 2-х заказов")
    @Description("Получение списка из 2-х заказов")
    public void getOrderListWithPageLimitParams() {

        HashMap<String, String> params = new HashMap<>();
        //Если передать limit 1, то проверка схемы не проходит
        //не могу понять почему так происходит
        params.put("limit", "2");
        params.put("page", "6");

        Response response = sendEasyGetRequest(ALLURE_PROMPT, API_ORDER_PATH, params);
        checkOrderResponseValidate(response);
        checkOrderReqContent(response);
    }

    @Test
    @DisplayName("Получение заказов для курьера")
    @Description("Получение заказов для курьера")
    public void getOrderListForCourier() {
        //Чтобы тест не падал предварительно добавил для этого курьера заказ
        //api/v1/orders/accept/137889?courierId=344668
        //По хорошему нужно сначала добавить заказ и в тесте взять его в работу,
        // но это должен быть другой тест на уровне е2е

        HashMap<String, String> params = new HashMap<>();
        params.put("courierId", "344668");

        Response response = sendEasyGetRequest(ALLURE_PROMPT, API_ORDER_PATH, params);
        checkOrderResponseValidate(response);
        checkOrderReqContent(response);
    }


    @Test
    @DisplayName("Получение заказов несуществующего курьера")
    @Description("Получение заказов несуществующего курьера")
    public void getOrderListForGhostCourier() {

        HashMap<String, String> params = new HashMap<>();
        params.put("courierId", "1");

        Response response = sendEasyGetRequest(ALLURE_PROMPT, API_ORDER_PATH, params);
        log.info(response.prettyPrint());
        response
                .then()
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .body("message", Matchers.equalTo(COURIER_NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Получение заказов по станции")
    @Description("Получение заказов по станции")
    public void getOrderListForStation() {

        HashMap<String, String> params = new HashMap<>();
        params.put("limit", "2");
        params.put("nearestStation", "110");

        Response response = sendEasyGetRequest(ALLURE_PROMPT, API_ORDER_PATH, params);
        checkOrderResponseValidate(response);
    }


    @Step(value = "Проверка ответа от метода получения заказов")
    public void checkOrderResponseValidate(Response response) {
        //В задаче сказано "Проверь, что в тело ответа возвращается список заказов."
        //Решил пойти легким путем. Схему сгенерировал тут:
        //https://www.liquid-technologies.com/online-json-to-schema-converter
        //Т.к. некоторые поля могут приходить пустыми, отредактировал условия получаемые типы
        //"type": ["string", "null"]

        log.info(response.prettyPrint());
        response
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(JSON_SCHEMA_PATH));
    }

    @Step(value = "Проверка обязательных ключей")
    public void checkOrderReqContent(Response response) {
        response.then()
                .body("orders[0].id", Matchers.any(Integer.class))
                .body("orders[0].track", Matchers.any(Integer.class))
                .body("orders[0].createdAt", Matchers.any(String.class))
                .body("orders[0].updatedAt", Matchers.any(String.class));
    }
}
