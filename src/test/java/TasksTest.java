import io.restassured.response.Response;
import models.*;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class TasksTest extends TestBase {
    private static final String TOKEN  = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MmVhYmZjMmZiN2NiMTAwMTdjZTU1ZGIiLCJpYXQiOjE2NTk1NTk4MTl9.vIBdJaBMHt7PXt4WpXDw9CgfCNwU9WXHdRUQFWSmXf8";

    /*Verificar el comportamiento de creación de tareas cuando no se envía el dato requerido “description”*/
    @Test
    public void registrar_tarea_deberia_devolver_400_y_mensaje_error_si_no_se_envia_description(){
        Task testTask = createNewEmptyTask();

        Response taskRegisterResponse = REQUEST.header("Authorization", "Bearer " + TOKEN).body(testTask).post("/task");
        taskRegisterResponse.then()
                .assertThat()
                .statusCode(400)
        ;

        String message = taskRegisterResponse.then().extract().asString();

        assertThat(message, containsString("Path `description` is required"));
    }

    private Task createNewEmptyTask(){
        return new Task();
    }

    private Task createNewTaskWithoutDescription(){
        return new Task("");
    }

    private Task createNewTask(){
        return new Task(FAKER.backToTheFuture().quote());
    }



}
