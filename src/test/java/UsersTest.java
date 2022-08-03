import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import models.User;
import models.UserAccount;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class UsersTest extends TestBase {

    /*Verificar el comportamiento del servicio de creación de usuarios cuando se registra un correo ya existente*/
    @Test
    public void creacion_usuarios_deberia_retornar_400_y_mensaje_error_al_registrar_correo_existente() {
        User testUser = createNewUser();
        REQUEST.body(testUser).post("/user/register");
        Response userRegisterRensponse = REQUEST.body(testUser).post("/user/register");
        userRegisterRensponse.then().assertThat().statusCode(400);
        String message = userRegisterRensponse.then().extract().asString();

        assertThat(message, containsString("duplicate key error collection"));
    }

    /*Verificar el comportamiento del servicio de creación de usuarios cuando se envía un email cuyo formato no es el correcto para correos electrónicos*/
    @Test
    public void creacion_usuarios_deberia_retornar_400_y_mensaje_error_al_registrar_correo_sin_formato() {
        User testUser = createNewUserWrongEmail();
        REQUEST.body(testUser).post("/user/register");
        Response userRegisterRensponse = REQUEST.body(testUser).post("/user/register");
        userRegisterRensponse.then().assertThat().statusCode(400);
        String message = userRegisterRensponse.then().extract().asString();

        assertThat(message, containsString("Email is invalid"));
    }

    /*Verificar el comportamiento del servicio de creación de usuarios cuando se registra un correo nuevo*/
/*emptycode here*/
    /*Verificar el comportamiento del servicio de Login cuando se envía un correo electrónico vacío*/
    @Test
    public void login_deberia_retornar_400_y_mensaje_error_al_enviar_correo_vacio() {

        UserAccount testUser = new UserAccount();
        testUser.password = FAKER.internet().password();

        Response userRegisterRensponse = REQUEST.body(testUser).post("/user/login");
        userRegisterRensponse.then().assertThat().statusCode(400);
        String message = userRegisterRensponse.then().extract().asString();

        assertThat(message, containsString("Unable to login"));
    }

    /*Verificar el comportamiento del servicio de Login cuando se envía contraseña vacío*/
    @Test
    public void login_deberia_retornar_400_y_mensaje_error_al_enviar_contrasenia_vacio() {

        UserAccount testUser = new UserAccount();
        testUser.email = FAKER.internet().emailAddress();

        Response userRegisterRensponse = REQUEST.body(testUser).post("/user/login");
        userRegisterRensponse.then().assertThat().statusCode(400);
        String message = userRegisterRensponse.then().extract().asString();

        assertThat(message, containsString("Unable to login"));
    }

    /*Verificar el comportamiento del servicio de Login usando las credenciales del nuevo usuario creado*/
    @Test
    public void login_deberia_retornar_200_y_response_con_estructura_establecida_y_con_datos_registrados_del_usuario() {

        User testUser = createNewUser();
        REQUEST.body(testUser).post("/user/register");

        UserAccount testLogin = new UserAccount(testUser.email, testUser.password);
        Response userLoginRensponse = REQUEST.body(testLogin).post("/user/login");
        userLoginRensponse.then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(matchesJsonSchemaInClasspath("loginResponse.json"))
                .and()
                .body("user.age", equalTo(testUser.age))
                .body("user.name", equalTo(testUser.name))
                .body("user.email", equalTo(testUser.email))
        ;
    }

    /*Verificar el comportamiento del servicio de Login usando las credenciales del nuevo usuario creado*/
    @Test
    public void logout_deberia_retornar_200_y_response_success() {

        User testUser = createNewUser();
        REQUEST.body(testUser).post("/user/register");

        UserAccount testLogin = new UserAccount(testUser.email, testUser.password);
        Response userLoginRensponse = REQUEST.body(testLogin).post("/user/login");

        String token = userLoginRensponse.body().jsonPath().getString("token");
        Response userLogoutResponse = REQUEST.header("Authorization", "Bearer "+token).post("/user/logout");
        userLogoutResponse.then().assertThat().statusCode(200).and().body("success", equalTo(true));
    }

    /*Verificar el comportamiento del servicio de logout utilizando el Bearer token obtenido del login luego de haberse ejecutado el servicio logout previamente*/
    @Test
    public void segundo_logout_seguido_deberia_retornar_401_y_response_error() {

        User testUser = createNewUser();
        REQUEST.body(testUser).post("/user/register");

        UserAccount testLogin = new UserAccount(testUser.email, testUser.password);
        Response userLoginRensponse = REQUEST.body(testLogin).post("/user/login");

        String token = userLoginRensponse.body().jsonPath().getString("token");
        REQUEST.header("Authorization", "Bearer "+token).post("/user/logout");
        Response userLogoutResponse = REQUEST.header("Authorization", "Bearer "+token).post("/user/logout");
        userLogoutResponse.then().assertThat().statusCode(401).and().body("error", notNullValue()).and().body("error", not(equalTo("")));
    }

    /*Verificar el comportamiento del servicio para obtener los datos del usuario luego de que se haya creado.*/
    /*DUDA DEL SERIVICIO A EVALUAR*/

    /*Verificar el comportamiento de actualización de datos del usuario cuando se actualiza un único dato del perfil del usuario como el nombre, la edad o el correo electrónico*/
    @Test
    public void update_user_deberia_retornar_200_y_modificar_el_dato_nombre(){



    }
    @Test
    public void update_user_deberia_retornar_200_y_modificar_el_dato_edad(){

    }
    @Test
    public void update_user_deberia_retornar_200_y_modificar_el_dato_correo(){

    }

    private User createNewUser() {
        return new User(FAKER.name().fullName(), FAKER.internet().emailAddress(), FAKER.internet().password(), FAKER.number().numberBetween(0, 99));
    }

    private User createNewUserWrongEmail() {
        return new User(FAKER.name().fullName(), FAKER.internet().avatar(), FAKER.internet().password(), FAKER.number().numberBetween(0, 99));
    }


}
