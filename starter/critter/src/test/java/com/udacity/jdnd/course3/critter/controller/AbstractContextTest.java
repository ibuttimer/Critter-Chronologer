package com.udacity.jdnd.course3.critter.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public abstract class AbstractContextTest extends AbstractTest {

    @LocalServerPort
    private Integer port;

    @BeforeAll
    public static void beforeAll() {
        AbstractTest.beforeAll();
    }

    @AfterAll
    public static void afterAll() {
        AbstractTest.afterAll();
    }

    protected String getLocalhostUrl(String path,
                                     String query,
                                     String fragment) {
        URI uri = null;
        try {
            uri = new URI("http", null, "localhost", port, path, query, fragment);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }
        return uri.toString();
    }

    protected String getLocalhostUrl(String path) {
        return getLocalhostUrl(path, null, null);
    }

}
