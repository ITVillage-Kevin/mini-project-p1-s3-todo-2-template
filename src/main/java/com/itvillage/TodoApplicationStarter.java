package com.itvillage;

import com.itvillage.config.ObjectFactory;

public class TodoApplicationStarter {
    public static void main(String[] args) {
        ObjectFactory objectFactory = ObjectFactory.getInstance();

        TodoApplication todoApplication = new TodoApplication(objectFactory);
        todoApplication.run();
    }
}
