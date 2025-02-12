package com.itvillage.repository;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.itvillage.common.converter.BiDirectionalConverter;
import com.itvillage.config.GoogleTasksInitializer;
import com.itvillage.entity.Todo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class GoogleTasksRepository implements TodoRepository<String> {
    private final Tasks service;
    private final TaskList defaultTaskList;
    private final BiDirectionalConverter<Todo<String>, Task> converter;

    public GoogleTasksRepository(BiDirectionalConverter<Todo<String>, Task> converter) throws GeneralSecurityException, IOException {
        service = GoogleTasksInitializer.initialize();
        this.defaultTaskList = service.tasklists().list().setMaxResults(10).execute().getItems().get(0);
        this.converter = converter;
    }

    @Override
    public Todo<String> save(Todo<String>  todo) {
        Task task = converter.convert(todo);

        try {
            if (isNew(todo)) {
                // 할 일을 Google Task 서버에 등록한다.
                service.tasks().insert(defaultTaskList.getId(), task).execute();
            } else {
                // 완료 처리된 할 일을 Google Task 서버에 업데이트 한다.
                service.tasks().update(defaultTaskList.getId(), task.getId(), task).execute();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return todo;
    }

    @Override
    public List<Todo<String>> findAll() {
        try {
            List<Task> tasks = service.tasks().list(defaultTaskList.getId()).execute().getItems();

            return tasks.stream()
                    .map(converter::revert)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Todo<String> findByTodo(Todo<String> todo) {
        // TODO: Google Tasks API를 이용해서 한 건의 특정 할일 조회
        return null;
    }
}
