package com.itvillage.common.converter;

import com.google.api.services.tasks.model.Task;
import com.itvillage.common.utils.DateTimeUtils;
import com.itvillage.entity.Todo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GoogleTasksConverter implements BiDirectionalConverter<Todo<String>, Task>{
    @Override
    public Task convert(Todo<String> todo) {
        LocalDateTime dateTime = DateTimeUtils.localDateToLocalDateTime(todo.getDueDate());
        String dueDate = DateTimeUtils.formatTimestampWithUtcZoneOffset(dateTime);

        String taskState = todo.getTaskState() == Todo.TaskState.INCOMPLETE ? "needsAction" : "completed";

        Task task = new Task();
        task.setId(todo.getTodoId());
        task.setTitle(todo.getTask());
        task.setDue(dueDate);
        task.setStatus(taskState);

        return task;
    }

    @Override
    public Todo<String> revert(Task task) {
        LocalDate dueDate = DateTimeUtils.rfc3339ToLocalDate(task.getDue());
        Todo.TaskState taskState = task.getStatus().equals("needsAction") ? Todo.TaskState.INCOMPLETE : Todo.TaskState.COMPLETE;

        Todo<String> todo =
                new Todo<>(task.getTitle(), dueDate, taskState);
        todo.addTodoId(task.getId());

        return todo;
    }
}
