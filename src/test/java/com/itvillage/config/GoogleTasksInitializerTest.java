package com.itvillage.config;

import com.google.api.services.tasks.Tasks;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName(("Google Task 초기화 테스트"))
public class GoogleTasksInitializerTest {

    @DisplayName("Google Tasks 초기화 테스트")
    @Test
    public void initializeTest() throws GeneralSecurityException, IOException {
        Tasks service = GoogleTasksInitializer.initialize();

        assertThat(service, is(notNullValue()));
    }
}
