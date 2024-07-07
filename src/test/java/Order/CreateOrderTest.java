package Order;

import com.github.javafaker.Faker;
import dto.Order;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import util.BaseScenario;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.apache.http.HttpStatus.SC_CREATED;


@Slf4j
@DisplayName("Создание заказа")
public class CreateOrderTest extends BaseScenario {

    private final static String API_CREATE_ORDER = "api/v1/orders";

    Faker faker = new Faker(new Locale("ru_Ru", "RU"));

    @ParameterizedTest(name = "Создание заказа {index} с цветом {0}")
    @DisplayName("Создание заказа")
    @Description("Создание заказа")
    @ValueSource(strings = {"GREY", "BLACK", "GREY, BLACK", ""})
    public void createOrderTest(String colors) {

        String[] color = colors.split("\\s*,\\s*"); //конвертирует в массив строк

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Order order = Order
                .builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .address(faker.address().streetAddress())
//                .metroStation(faker.number().numberBetween(1,10))
                .metroStation(faker.random().toString())
                .phone(faker.phoneNumber().phoneNumber())
                .rentTime(faker.number().numberBetween(1,30))
                .deliveryDate(sdf.format(faker.date().future(10,TimeUnit.DAYS)))
                .comment("created by autotest")
                .color(color)
                .build();

        Response response = sendPostRequest("Создание заказа", order, API_CREATE_ORDER);

        log.info(response.prettyPrint());
        response.then()
                .assertThat().body("track", Matchers.any(Integer.class))
                .and()
                .statusCode(SC_CREATED);
    }
}
