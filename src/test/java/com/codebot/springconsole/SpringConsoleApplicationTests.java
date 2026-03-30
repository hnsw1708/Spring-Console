package com.codebot.springconsole;

import com.codebot.springconsole.application.SpringConsoleApplication;
import com.codebot.springconsole.component.Runner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = SpringConsoleApplication.class)
public class SpringConsoleApplicationTests {

    @Autowired
    private Runner runner;

    @Test
    public void contextLoads() {
        assertThat(runner).isNotNull();
    }

}
