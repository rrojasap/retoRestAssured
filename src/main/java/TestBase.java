import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.util.Properties;

public class TestBase {

    public RequestSpecification REQUEST;
    public Faker FAKER = new Faker();

    public TestBase() {
        try {
            Properties props = new Properties();
            props.load(getClass().getClassLoader().getResourceAsStream("config.properties"));

            RestAssured.baseURI = props.getProperty("api.uri");
        }catch ( IOException e ){
            e.printStackTrace();
        }

        REQUEST = RestAssured.given().contentType(ContentType.JSON);
    }


}
