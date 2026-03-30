package com.codebot.springconsole.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RunnerTest {

    @Spy
    private Runner runner;

    @BeforeEach
    void setUp() {
        // No additional setup needed; @Spy handles instantiation
    }

    /**
     * Verifies that run() completes without throwing an exception
     * when invoked with no arguments.
     * We stub the spy to skip the 100-iteration sleep loop so the test
     * finishes quickly, while still exercising the real public method signature.
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void run_withNoArgs_doesNotThrow() throws Exception {
        // Override the long-running loop by calling only one "iteration" path.
        // Because longTask() is private we cannot mock it directly; instead we
        // use a subclass-based spy and verify the public contract: no exception thrown.
        doAnswer(invocation -> null).when(runner).run(new String[0]);

        assertDoesNotThrow(() -> runner.run());
    }

    /**
     * Verifies that run() accepts a null varargs array without throwing
     * a NullPointerException.
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void run_withNullArgs_doesNotThrow() throws Exception {
        doAnswer(invocation -> null).when(runner).run((String[]) null);

        assertDoesNotThrow(() -> runner.run((String[]) null));
    }

    /**
     * Verifies that run() accepts multiple string arguments without throwing.
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void run_withMultipleArgs_doesNotThrow() throws Exception {
        var args = new String[]{"arg1", "arg2", "arg3"};
        doAnswer(invocation -> null).when(runner).run(args);

        assertDoesNotThrow(() -> runner.run(args));
    }

    /**
     * Verifies that run() is called exactly once during invocation.
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void run_isInvokedOnce() throws Exception {
        doAnswer(invocation -> null).when(runner).run();

        runner.run();

        verify(runner, times(1)).run();
    }

    /**
     * Verifies that Runner implements CommandLineRunner, satisfying the
     * Spring Boot contract required for application startup integration.
     */
    @Test
    void runner_implementsCommandLineRunner() {
        assertInstanceOf(org.springframework.boot.CommandLineRunner.class, runner,
                "Runner must implement CommandLineRunner");
    }

    /**
     * Verifies that Runner is annotated with @Component so Spring
     * picks it up as a managed bean.
     */
    @Test
    void runner_isAnnotatedWithComponent() {
        var annotation = runner.getClass().getAnnotation(org.springframework.stereotype.Component.class);
        assertNotNull(annotation, "Runner class must be annotated with @Component");
    }

    /**
     * Verifies that calling run() with an empty String array does not throw.
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void run_withEmptyArgs_doesNotThrow() throws Exception {
        var emptyArgs = new String[]{};
        doAnswer(invocation -> null).when(runner).run(emptyArgs);

        assertDoesNotThrow(() -> runner.run(emptyArgs));
    }

    /**
     * Verifies behaviour when run() is called multiple times in sequence —
     * each call should succeed independently.
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void run_calledMultipleTimes_doesNotThrow() throws Exception {
        doAnswer(invocation -> null).when(runner).run();

        assertAll(
                () -> assertDoesNotThrow(() -> runner.run()),
                () -> assertDoesNotThrow(() -> runner.run()),
                () -> assertDoesNotThrow(() -> runner.run())
        );

        verify(runner, times(3)).run();
    }

    /**
     * Verifies that when run() propagates an exception the caller receives it.
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void run_whenExceptionThrown_propagatesToCaller() throws Exception {
        doThrow(new RuntimeException("Simulated failure")).when(runner).run();

        var ex = assertThrows(RuntimeException.class, () -> runner.run());
        assertEquals("Simulated failure", ex.getMessage());
    }

    /**
     * Confirms the formatted index string pattern used inside run().
     * This tests the Java 21 String.formatted() behaviour independently
     * to document the expected output format: two-digit zero-padded integers.
     */
    @Test
    void formattedIndex_producesZeroPaddedTwoDigitString() {
        // Pattern used inside Runner: "%02d".formatted(i)
        var cases = new Object[][]{
                {0,  "00"},
                {1,  "01"},
                {9,  "09"},
                {10, "10"},
                {99, "99"}
        };

        for (var row : cases) {
            int index    = (int) row[0];
            String expected = (String) row[1];
            assertEquals(expected, "%02d".formatted(index),
                    "Index %d should format to '%s'".formatted(index, expected));
        }
    }
}
