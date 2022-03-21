package com.example.expressionsevaluator.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.apache.commons.io.IOUtils.toByteArray;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpressionsEvaluatorControllerTest {

    public static final String SAVE_PATH = "/api/v1/expressions";
    public static final String EVAL_PATH = "/api/v1/expressions/calories/calculations";

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mvc;

    @BeforeAll
    public void setup() {
        mvc = webAppContextSetup(ctx).build();
    }

    @Test
    public void evaluateTest() throws Exception {
        mvc.perform(
                post(SAVE_PATH).contentType(APPLICATION_JSON)
                        .content(
                                new String(
                                    toByteArray(new ClassPathResource("/saveExpression.json").getInputStream()),
                                    StandardCharsets.UTF_8)
                        )
        ).andExpect(status().isOk());
        mvc.perform(
                post(EVAL_PATH).contentType(APPLICATION_JSON)
                        .content(new String(
                                    toByteArray(new ClassPathResource("/evaluate.json").getInputStream()),
                                    StandardCharsets.UTF_8)
                        )
        ).andExpect(status().isOk()).andExpect(jsonPath("$.value").value("2"));
    }
}