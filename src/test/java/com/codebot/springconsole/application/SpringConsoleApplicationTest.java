package com.codebot.springconsole.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringConsoleApplicationTest {

    /**
     * Verifies that the main method executes without throwing any exception
     * when SpringApplication.run is mocked to avoid a real Spring context startup.
     */
    @Test
    void main_shouldRunWithoutException_whenCalledWithEmptyArgs() {
        try (MockedStatic<SpringApplication> mockedSpringApp = Mockito.mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
            mockedSpringApp
                    .when(() -> SpringApplication.run(eq(SpringConsoleApplication.class), any(String[].class)))
                    .thenReturn(mockContext);

            assertDoesNotThrow(() -> SpringConsoleApplication.main(new String[]{}));

            mockedSpringApp.verify(
                    () -> SpringApplication.run(eq(SpringConsoleApplication.class), eq(new String[]{})),
                    times(1)
            );
        }
    }

    /**
     * Verifies that the main method correctly forwards provided arguments
     * to SpringApplication.run.
     */
    @Test
    void main_shouldForwardArgs_toSpringApplication() {
        String[] args = {"--server.port=8080", "--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> mockedSpringApp = Mockito.mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
            mockedSpringApp
                    .when(() -> SpringApplication.run(eq(SpringConsoleApplication.class), any(String[].class)))
                    .thenReturn(mockContext);

            assertDoesNotThrow(() -> SpringConsoleApplication.main(args));

            mockedSpringApp.verify(
                    () -> SpringApplication.run(eq(SpringConsoleApplication.class), eq(args)),
                    times(1)
            );
        }
    }

    /**
     * Verifies that the main method handles a null args array gracefully
     * (null-safe delegation to SpringApplication.run).
     */
    @Test
    void main_shouldHandleNullArgs_withoutException() {
        try (MockedStatic<SpringApplication> mockedSpringApp = Mockito.mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
            mockedSpringApp
                    .when(() -> SpringApplication.run(eq(SpringConsoleApplication.class), any()))
                    .thenReturn(mockContext);

            // Passing null simulates an edge case; the method itself does not null-check args
            assertDoesNotThrow(() -> SpringConsoleApplication.main(null));
        }
    }

    /**
     * Verifies that SpringApplication.run is invoked exactly once per main() call,
     * ensuring no duplicate context startups occur.
     */
    @Test
    void main_shouldInvokeSpringApplicationRun_exactlyOnce() {
        try (MockedStatic<SpringApplication> mockedSpringApp = Mockito.mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
            mockedSpringApp
                    .when(() -> SpringApplication.run(eq(SpringConsoleApplication.class), any(String[].class)))
                    .thenReturn(mockContext);

            SpringConsoleApplication.main(new String[]{});

            mockedSpringApp.verify(
                    () -> SpringApplication.run(eq(SpringConsoleApplication.class), any(String[].class)),
                    times(1)
            );
            mockedSpringApp.verifyNoMoreInteractions();
        }
    }

    /**
     * Verifies class-level annotations are present, confirming the application
     * is correctly declared as a Spring Boot application with component scanning.
     */
    @Test
    void springConsoleApplication_shouldHaveRequiredAnnotations() {
        var annotations = SpringConsoleApplication.class.getAnnotations();

        boolean hasSpringBootApplication = false;
        boolean hasComponentScan = false;

        for (var annotation : annotations) {
            String name = annotation.annotationType().getSimpleName();
            if ("SpringBootApplication".equals(name)) {
                hasSpringBootApplication = true;
            }
            if ("ComponentScan".equals(name)) {
                hasComponentScan = true;
            }
        }

        assert hasSpringBootApplication : "@SpringBootApplication annotation is missing";
        assert hasComponentScan : "@ComponentScan annotation is missing";
    }
}
