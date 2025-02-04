package com.itvillage;

import com.itvillage.common.console.ConsoleInputHandler;
import com.itvillage.common.console.ConsoleMenu;
import com.itvillage.common.console.ConsolePrinter;
import com.itvillage.common.converter.GoogleTasksConverter;
import com.itvillage.handler.TodoConsoleHandler;
import com.itvillage.repository.GoogleTasksRepository;
import com.itvillage.repository.InMemoryTodoRepository;
import com.itvillage.service.TodoService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.NoSuchElementException;

import static com.itvillage.common.console.ConsolePrinter.println;

public class TodoApplication {
    private final static TodoService<String> todoService;
    private final static TodoConsoleHandler<String> todoConsoleHandler;

    static {
        try {
            todoService = new TodoService<>(new GoogleTasksRepository(new GoogleTasksConverter()));
            todoConsoleHandler = new TodoConsoleHandler<>(todoService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public static void run() {
        println("⤴️ 하위 메뉴에서 이전 메뉴로 돌아가기 위해서는 `:q`를 입력한 후 엔터키를 누르세요.");
        while (true) {
            try {
                // (1) 할 일 관리 메뉴 조회
                String todoMenu = ConsoleMenu.getMenuDescription();

                // (2) 할 일 관리 메뉴 출력
                ConsolePrinter.println(todoMenu);

                // (3) 할 일 관리 메뉴 선택
                selectMenu();
            } catch (NoSuchElementException ex) {
                System.out.println("애플리케이션을 종료합니다.");
                ConsoleInputHandler.close();
                System.exit(0);
            } catch (Exception ex) {
                ConsolePrinter.println(ex.getMessage());
            }
        }
    }

    private static void selectMenu() {
        String selectedMenuNumber = ConsoleInputHandler.inputValue();

        // TodoConsoleHandler 클래스의 메서드를 호출하는 로직이 추가되어야 합니다.
        todoConsoleHandler.handle(selectedMenuNumber);
    }
}
