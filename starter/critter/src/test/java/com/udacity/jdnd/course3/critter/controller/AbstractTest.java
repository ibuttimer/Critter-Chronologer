package com.udacity.jdnd.course3.critter.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public abstract class AbstractTest implements ITestResource {

    @Autowired
    protected MockMvc mockMvc;

    protected ResourceBundle bundle;

    public AbstractTest() {
        bundle = getResourceBundle("test");
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return bundle;
    }

    @BeforeAll
    public static void beforeAll() {
        // no-op
    }

    @AfterAll
    public static void afterAll() {
        // no-op
    }

    protected void clearRepository(CrudRepository<?, Long> repository) {
        repository.deleteAll();
        assertFalse(repository.findAll().iterator().hasNext(), () -> "Repository not empty: " + repository.getClass().getSimpleName());
    }

}
