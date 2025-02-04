package com.itvillage.common.converter;

import com.google.api.services.tasks.model.Task;
import com.itvillage.common.utils.DateTimeUtils;
import com.itvillage.entity.Todo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("GoogleTasksConverter 테스트")
public class GoogleTasksConverterTest {
    private static BiDirectionalConverter<Todo<String>, Task> googleTasksConverter;

    @BeforeAll
    public static void beforeAll() {
        googleTasksConverter = new GoogleTasksConverter();
    }

    @DisplayName("Todo를 Task로 변환하는 테스트")
    @Test
    public void convertTest() {
        // given
        Todo<String> todo = new Todo<>("공부하기", LocalDate.now());

        // when
        Task task = googleTasksConverter.convert(todo);

        // then
        assertThat(task, notNullValue());
        assertThat(task.getTitle(), is(equalTo(todo.getTask())));
        assertThat(DateTimeUtils.rfc3339ToLocalDate(task.getDue()), is(todo.getDueDate()));
    }

    @DisplayName("Task를 Todo로 변환하는 테스트")
    @Test
    public void revertTest() {
        // given
        Task task = new Task();
        task.setId("sdfgsdfgsdfg");
        task.setTitle("공부하기");
        task.setStatus("needsAction");

        LocalDate dueDate = LocalDate.now().plusDays(5);
        LocalDateTime dueDateTime = dueDate.atStartOfDay();

        task.setDue(DateTimeUtils.formatTimestampWithUtcZoneOffset(dueDateTime));

        // when
        Todo<String> todo = googleTasksConverter.revert(task);

        // then
        String actualTaskState = todo.getTaskState() == Todo.TaskState.INCOMPLETE ? "needsAction" : "completed";

        assertThat(todo, notNullValue());
        assertThat(todo.getTask(), is(equalTo(task.getTitle())));
        assertThat(actualTaskState, is(equalTo(task.getStatus())));
        assertThat(todo.getDueDate(), is(DateTimeUtils.rfc3339ToLocalDate(task.getDue())));
    }
}
