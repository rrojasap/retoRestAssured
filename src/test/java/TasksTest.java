import io.restassured.response.Response;
import models.*;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class TasksTest extends TestBase {
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MmVhYmZjMmZiN2NiMTAwMTdjZTU1ZGIiLCJpYXQiOjE2NTk1NTk4MTl9.vIBdJaBMHt7PXt4WpXDw9CgfCNwU9WXHdRUQFWSmXf8";

    /*Verificar el comportamiento de creación de tareas cuando no se envía el dato requerido “description”*/
    @Test
    public void registrar_tarea_deberia_devolver_400_y_mensaje_error_si_se_envia_description_null() {
        Task testTask = createNewEmptyTask();

        Response taskRegisterResponse = REQUEST.header("Authorization", "Bearer " + TOKEN).body(testTask).post("/task");
        taskRegisterResponse.then()
                .assertThat()
                .statusCode(400)
        ;

        String message = taskRegisterResponse.then().extract().asString();

        assertThat(message, containsString("Path `description` is required"));
    }

    @Test
    public void registrar_tarea_deberia_devolver_400_y_mensaje_error_si_se_envia_description_vacio() {
        Task testTask = createNewTaskWithoutDescription();

        Response taskRegisterResponse = REQUEST.header("Authorization", "Bearer " + TOKEN).body(testTask).post("/task");
        taskRegisterResponse.then()
                .assertThat()
                .statusCode(400)
        ;

        String message = taskRegisterResponse.then().extract().asString();

        assertThat(message, containsString("Path `description` is required"));
    }

    @Test
    public void registrar_tarea_deberia_devolver_400_y_mensaje_error_si_no_se_envia_nada() {
        Response taskRegisterResponse = REQUEST.header("Authorization", "Bearer " + TOKEN).post("/task");
        taskRegisterResponse.then()
                .assertThat()
                .statusCode(400)
        ;

        String message = taskRegisterResponse.then().extract().asString();

        assertThat(message, containsString("Path `description` is required"));
    }

    /*Verificar el comportamiento del servicio de creación de tareas cuando se envía un valor para el campo “description”*/
    @Test
    public void registrar_tarea_deberia_devolver_201_valor_success_true_completed_false_y_descripcion_registrada() {
        Task testTask = createNewTask();
        Response taskRegisterResponse = REQUEST.header("Authorization", "Bearer " + TOKEN).body(testTask).post("/task");

        taskRegisterResponse.then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("data.completed", equalTo(false))
                .and()
                .body("data.description", equalTo(testTask.description))
        ;
    }

    /*Verificar el comportamiento del servicio que lista todas las tareas*/
    @Test
    public void listar_tareas_deberia_devolver_200_y_response_con_estructura_especificada(){
        REQUEST.header("Authorization", "Bearer " + TOKEN).get("/task")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(matchesJsonSchemaInClasspath("taskResponse.json"))
        ;
    }


    private Task createNewEmptyTask() {
        return new Task();
    }

    private Task createNewTaskWithoutDescription() {
        return new Task("");
    }

    private Task createNewTask() {
        return new Task(FAKER.backToTheFuture().quote());
    }


}
