/* package it;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

@QuarkusTest
public class CarTest {

    @Test
    public void databaseTest() {
        
        given()
                .when().get("car")
                .then()
                .statusCode(200)
                .body(
                        containsString("Chevrolet"),
                        containsString("Volkswagen"),
                        containsString("BMW"));

        //Delete the Chevrolet
        given()
                .when().delete("car" + "/1")
                .then()
                .statusCode(204);

        //List all, Chevrolet should be missing now:
        given()
                .when().get("car")
                .then()
                .statusCode(200)
                .body(
                        not(containsString("Cruze")),
                        containsString("Golf"),
                        containsString("X5"));
    }

} */