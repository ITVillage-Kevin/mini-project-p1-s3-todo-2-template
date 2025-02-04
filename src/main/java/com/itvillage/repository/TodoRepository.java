package com.itvillage.repository;

import com.itvillage.entity.Todo;

import java.util.List;

public interface TodoRepository<ID> {
    Todo<ID> save(Todo<ID> todo);
    List<Todo<ID>> findAll();
    Todo<ID> findByTodo(Todo<ID> todo);


    default boolean isNew(Todo<ID> todo) {
        return todo.getTodoId() == null;
    }
}
