package com.itvillage.googletasks;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("Google Tasks API 학습 테스트")
public class GoogleTasksApiTest {
    private static final String APPLICATION_NAME = "Google Tasks API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(TasksScopes.TASKS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static Tasks service;
    private static Task sampleTask;
    private static TaskList defaultTaskList;
    private Task insertedTask;

    @BeforeAll
    public static void beforeAll() throws IOException, GeneralSecurityException {
        initGoogleTasksApi();
        sampleTask = setSampleTask();
        defaultTaskList = getDefaultTaskList();
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        insertedTask = insertNewTask();
    }

    @AfterEach
    public void afterEach() throws IOException {
        deleteAllTasks();
    }


    // Google Tasks API를 이용한 List<TaskList>를 조회 테스트
    @DisplayName("TaskList 목록 조회 테스트")
    @Test
    public void getTaskListsTest() throws IOException, GeneralSecurityException{
        // given

        // when
        TaskLists result = service.tasklists().list()
                .setMaxResults(10)
                .execute();
        List<TaskList> taskLists = result.getItems();

        // then
        assertThat(taskLists.size(), greaterThanOrEqualTo(1));

//        if (taskLists == null || taskLists.isEmpty()) {
//            System.out.println("No task lists found.");
//        } else {
//            System.out.println("Task lists:");
//            for (TaskList tasklist : taskLists) {
//                System.out.printf("%s (%s)\n", tasklist.getTitle(), tasklist.getId());
//            }
//        }
    }

    // Google Tasks API를 이용한 할 일 등록 테스트
    @DisplayName("TaskList에 Task 추가 테스트")
    @Test
    public void insertTaskTest() throws IOException {
        // given
        Task newTask = sampleTask;

        // when
        Task insertedTask = service.tasks().insert(defaultTaskList.getId(), newTask).execute();

        // then
        assertThat(insertedTask.getTitle(), is(equalTo(newTask.getTitle())));
        assertThat(insertedTask.getDue(), is(equalTo(newTask.getDue())));
    }

    // Google Tasks API를 이용한 할 일 목록 조회 테스트
    @DisplayName("TaskList에 포함된 Task 목록 조회 테스트")
    @Test
    public void getTasksTest() throws IOException {
        // given
        String taskListId = defaultTaskList.getId();
        String taskId = insertedTask.getId();

        // when
        List<Task> tasks = service.tasks().list(taskListId).execute().getItems();

        // then
        assertThat(tasks.size(), is(1));
        assertThat(tasks.get(0).getId(), is(taskId));
    }

    // Google Tasks API를 이용한 한 건의 특정 할 일 조회 테스트
    @DisplayName("한 건의 Task 조회 테스트")
    @Test
    public void getTaskTest() throws IOException {
        // given
        String taskListId = defaultTaskList.getId();
        String taskId = insertedTask.getId();

        // when
        Task foundTask = findTaskBy(taskListId, taskId);

        // then
        assertThat(foundTask, is(notNullValue()));
        assertThat(foundTask.getId(), is(equalTo(taskId)));
    }

    // Google Tasks API를 이용한 특정 할 일에 대한 완료 처리 테스트
    @DisplayName("Task 완료 처리 테스트")
    @Test
    public void completeTaskTest() throws IOException {
        // given
        String taskListId = defaultTaskList.getId();
        String taskId = insertedTask.getId();
        Task foundTask = findTaskBy(taskListId, taskId);

        // when
        String taskCompleted = "completed";
        foundTask.setStatus(taskCompleted);
        service.tasks().update(taskListId, taskId, foundTask).execute();

        // then
        Task updatedTask = findTaskBy(taskListId, taskId);
        assertThat(updatedTask, is(notNullValue()));
        assertThat(updatedTask.getStatus(), is(equalTo(taskCompleted)));
    }

    // Google Tasks API를 이용한 한 건의 특정 할 일 삭제 테스트
    @DisplayName("한 건의 Task 삭제 테스트")
    @Test
    public void deleteTaskTest() throws IOException {
        // given
        String taskListId = defaultTaskList.getId();
        String taskId = insertedTask.getId();

        // when
        service.tasks().delete(taskListId, taskId).execute();

        // then
        /**
         * 1. 추가한 Task의 id에 해당되는 Task를 가지고 온다.
         * 2. 조회한 Task의 상태 값이 삭제 상태인지를 검증을 한다.
         */
        Task deletedTask = service.tasks().get(defaultTaskList.getId(), insertedTask.getId()).execute();
        assertThat(deletedTask.getDeleted(), is(true));
    }

    // Google Tasks API를 이용한 모든 할 일 삭제 테스트
    @DisplayName("모든 Task 삭제 테스트")
    @Test
    public void deleteAllTasksTest() throws IOException {
        // given
        String taskListId = defaultTaskList.getId();
        List<Task> tasks = service.tasks().list(taskListId).execute().getItems();

        // when
        for (Task deleteTask : tasks) {
            service.tasks().delete(defaultTaskList.getId(), deleteTask.getId()).execute();
        }

        // then
        List<Task> deletedTasks = service.tasks().list(defaultTaskList.getId()).execute().getItems();
        assertThat(deletedTasks.isEmpty(), is(true));
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = GoogleTasksApiTest.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static void initGoogleTasksApi() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static TaskList getDefaultTaskList() throws IOException {
        TaskLists result = service.tasklists().list()
                .setMaxResults(10)
                .execute();

        return result.getItems().get(0);
    }

    private static Task setSampleTask() {
        Task task = new Task();
        LocalDate date = LocalDate.now().plusDays(3);
        LocalDateTime dateTime = date.atStartOfDay();
        String dueDate = dateTime.atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));

        // 할 일의 내용, 할 일 수행 날짜
        task.setTitle("잠자기");
        task.setNotes("최소한 8시간은 자기");
        task.setDue(dueDate);

        return task;
    }

    private Task insertNewTask() throws IOException {
        return service.tasks().insert(defaultTaskList.getId(), sampleTask).execute();
    }

    private void deleteAllTasks() throws IOException {
        List<Task> tasks = service.tasks().list(defaultTaskList.getId()).execute().getItems();

        for (Task deleteTask : tasks) {
            service.tasks().delete(defaultTaskList.getId(), deleteTask.getId()).execute();
        }
    }

    private Task findTaskBy(String taskListId, String taskId) throws IOException {
        return service.tasks().get(taskListId, taskId).execute();
    }
}
