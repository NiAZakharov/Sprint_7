package util;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import util.ConnectionProperty;
import util.PropertyLoader;

public class BaseScenario {

    protected static RequestSpecification requestSpecification;
    protected static ResponseSpecification responseSpecification;

    private static final ConnectionProperty CONNECTION_PROPERTIES = PropertyLoader.loadProperties();


    @BeforeAll
    public static void setUp() {

        requestSpecification = RestAssured
                .given()
                .baseUri(CONNECTION_PROPERTIES.getHost())
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all();

        RestAssured.requestSpecification = requestSpecification;


//        responseSpecification =RestAssured.responseSpecification.response().log().all();
//        responseSpecification = RestAssured.responseSpecification.response().log().all();
//        RestAssured.responseSpecification = responseSpecification;

    }


    @AfterAll
    public static void tearDown(){
        RestAssured.reset();
    }
}
