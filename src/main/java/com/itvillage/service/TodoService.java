package com.itvillage.service;

import com.itvillage.entity.Todo;
import com.itvillage.repository.TodoRepository;

import java.util.List;

public class TodoService<ID> {
    private final TodoRepository<ID> todoRepository;

    public TodoService(TodoRepository<ID> todoRepository) {
        this.todoRepository = todoRepository;
    }

    // 할 일 등록 처리
    public Todo<ID> registerTodo(Todo<ID> todo) {
        todo.verifyValidDueDate();

        return todoRepository.save(todo);
    }

    // 한 건의 특정 할일을 조회
    public Todo<ID> findTodoBy(Todo<ID> todo) {
        return todoRepository.findByTodo(todo);
    }

    // 할 일 목록 조회
    public List<Todo<ID>> findAllTodos() {
        return todoRepository.findAll();
    }

    // 할 일 완료 처리
    public void completeTodo(Todo<ID> todo, Todo.TaskState taskState) {
        Todo<ID> foundTodo = todoRepository.findByTodo(todo);

        foundTodo.completeTask(taskState);

        todoRepository.save(foundTodo);
    }


}
