package com.itvillage.config;

import com.itvillage.common.converter.GoogleTasksConverter;
import com.itvillage.handler.TodoConsoleHandler;
import com.itvillage.repository.GoogleTasksRepository;
import com.itvillage.service.TodoService;

import java.util.HashMap;
import java.util.Map;

public class ObjectFactory {
    private final static ObjectFactory instance = new ObjectFactory();
    private final Map<Class<?>, Object> objects = new HashMap<>();

    private ObjectFactory() {
        // 애플리케이션에서 필요한 객체 생성.
        createObjects();
    }

    public static ObjectFactory getInstance() {
        return instance;
    }

    public <T> T getObject(Class<T> tClass) {
        return tClass.cast(objects.get(tClass));
    }

    private void createObjects() {
        try {
            TodoService<String> todoService =
                    new TodoService<>(new GoogleTasksRepository(new GoogleTasksConverter()));
            TodoConsoleHandler<String> todoConsoleHandler = new TodoConsoleHandler<>(todoService);

            objects.put(TodoConsoleHandler.class, todoConsoleHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
