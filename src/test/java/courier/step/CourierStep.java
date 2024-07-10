package courier.step;

import edu.practikum.util.BaseScenario;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.equalTo;

@Slf4j
public class CourierStep extends BaseScenario {


    @Step(value = "{4}")
    public void checkEasyResponse(Response response, int expectedStatusCode,
                                  String bodySingleKey, String expectedValue, String allurePromt) {
        log.info(response.prettyPrint());
        logResponseToAllure(response);
        response.then()
                .statusCode(expectedStatusCode)
                .and()
                .assertThat().body(bodySingleKey, equalTo(expectedValue));
    }

    @Step(value = "{4}")
    public void checkEasyResponse(Response response, int expectedStatusCode,
                                  String bodySingleKey, Boolean expectedValue, String allurePromt) {
        log.info(response.prettyPrint());
        logResponseToAllure(response);
        response.then()
                .statusCode(expectedStatusCode)
                .and()
                .assertThat().body(bodySingleKey, equalTo(expectedValue));
    }

    @Step(value = "{4}")
    public void checkEasyResponse(Response response, int expectedStatusCode,
                                  String bodySingleKey, String allurePromt) {
        log.info(response.prettyPrint());
        logResponseToAllure(response);
        response.then()
                .statusCode(expectedStatusCode)
                .and()
                .assertThat().body(bodySingleKey, Matchers.any(Integer.class));
    }

}
