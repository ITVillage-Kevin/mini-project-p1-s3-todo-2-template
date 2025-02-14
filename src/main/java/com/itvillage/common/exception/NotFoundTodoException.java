package com.itvillage.common.exception;

public class NotFoundTodoException extends RuntimeException {
    private final static String MESSAGE_NOT_FOUND_TODO = "# 할 일이 존재하지 않습니다.";

    public NotFoundTodoException() {
        super(MESSAGE_NOT_FOUND_TODO);
    }
}
