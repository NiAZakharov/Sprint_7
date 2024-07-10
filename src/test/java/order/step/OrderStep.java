package order.step;

import edu.practikum.util.BaseScenario;
import io.qameta.allure.Step;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;

import static org.apache.http.HttpStatus.SC_OK;

@Slf4j
public class OrderStep extends BaseScenario {

    @Step(value = "Проверка ответа от метода получения заказов")
    public void checkOrderResponseValidate(Response response, String jsonPath) {
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
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(jsonPath));
    }

    @Step(value = "Проверка обязательных ключей")
    public void checkOrderReqContent(Response response) {
        logResponseToAllure(response);
        response.then()
                .body("orders[0].id", Matchers.any(Integer.class))
                .body("orders[0].track", Matchers.any(Integer.class))
                .body("orders[0].createdAt", Matchers.any(String.class))
                .body("orders[0].updatedAt", Matchers.any(String.class));
    }

}
