import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import models.User;
import org.hamcrest.Matchers;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

public class UsersTest extends TestBase {

    @Test
    public void creacion_usuarios_deberia_retornar_400_y_mensaje_error_al_registrar_correo_existente() {
        User testUser = createNewUser();
        REQUEST.body(testUser).post("/user/register");
        Response userRegisterRensponse = REQUEST.body(testUser).post("/user/register");
        userRegisterRensponse.then().assertThat().statusCode(400);
        String message = userRegisterRensponse.then().extract().asString();

        assertThat(message, containsString("duplicate key error collection"));
    }

    @Test
    public void creacion_usuarios_deberia_retornar_400_y_mensaje_error_al_registrar_correo_sin_formato() {
        User testUser = createNewUserWrongEmail();
        REQUEST.body(testUser).post("/user/register");
        Response userRegisterRensponse = REQUEST.body(testUser).post("/user/register");
        userRegisterRensponse.then().assertThat().statusCode(400);
        String message = userRegisterRensponse.then().extract().asString();

        assertThat(message, containsString("Email is invalid"));
    }


    private User createNewUser() {
        return new User(FAKER.name().fullName(), FAKER.internet().emailAddress(), FAKER.internet().password(), FAKER.number().numberBetween(0, 99));
    }
    private User createNewUserWrongEmail() {
        return new User(FAKER.name().fullName(), FAKER.internet().avatar(), FAKER.internet().password(), FAKER.number().numberBetween(0, 99));
    }


}
