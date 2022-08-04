import io.restassured.response.Response;
import models.*;
import org.hamcrest.Matchers;
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

    /*Verificar el comportamiento del servicio de actualización de tareas cuando se envía el valor true para “completed”*/
    @Test
    public void update_tarea_deberia_devolver_200_valor_completed_true_cuando_se_envia_completed_true() {
        Task testTask = createNewTask();
        Response taskRegisterResponse = REQUEST.header("Authorization", "Bearer " + TOKEN).body(testTask).post("/task");

        String testId = taskRegisterResponse.body().jsonPath().getString("data._id");
        TaskCompleter testCompleter = createNewTaskCompleter();
        Response taskUpdateResponse = REQUEST.header("Authorization", "Bearer " + TOKEN).body(testCompleter).put("/task/"+testId);

        taskUpdateResponse.then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("data.completed", equalTo(true));
    }

    /*Verificar el comportamiento del servicio de listado de tareas con paginación enviando siempre un valor para el queryParam “skip” 0 y un número límite*/
    @Test
    public void get_tareas_paginadas_skip_0_deberia_devolver_200_items_listados_igual_a_count_y_count_menor_a_limit() {
        Integer testLimit = Math.toIntExact(FAKER.number().randomNumber());
        Response taskListResponse = REQUEST.header("Authorization", "Bearer " + TOKEN).get("/task?limit="+testLimit+"&skip=0");
        Integer listSize = taskListResponse.body().jsonPath().getList("data").size();

        taskListResponse.then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("count", equalTo(listSize))
                .and()
                .body("count", Matchers.lessThanOrEqualTo(testLimit));
    }

    /*Verificar el comportamiento del servicio de eliminación cuando se provee un id existente*/
    @Test
    public void delete_task_deberia_devolver_200_valor_success_true(){
        Task testTask = createNewTask();
        Response taskRegisterResponse = REQUEST.header("Authorization", "Bearer " + TOKEN).body(testTask).post("/task");

        String testId = taskRegisterResponse.body().jsonPath().getString("data._id");
        Response taskDeleteResponse = REQUEST.header("Authorization", "Bearer " + TOKEN).delete("/task/"+testId);

        taskDeleteResponse.then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    /*Verificar el comportamiento del servicio de eliminación de tareas cuando se provee un id que no existe.
       * El Sistema deberia devolver un codigo 400 de bad request
       * El Sistema deberia devolver un mensaje de error
    */
    @Test
    public void delete_task_deberia_devolver_400_mensaje_error_con_id_inexistente(){
        String testId = FAKER.backToTheFuture().quote();

        Response taskDeleteResponse = REQUEST.header("Authorization", "Bearer " + TOKEN).delete("/task/"+testId);

        taskDeleteResponse.then()
                .assertThat()
                .statusCode(400);

        String errorMessage = taskDeleteResponse.then().extract().asString();
        assertThat(errorMessage, not(equalTo("")));
        assertThat(errorMessage, notNullValue());
    }

    @Test
    public void aaa(){
        System.out.println(FAKER.internet().avatar());
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

    private TaskCompleter createNewTaskCompleter(){
        return new TaskCompleter(true);
    }


}
