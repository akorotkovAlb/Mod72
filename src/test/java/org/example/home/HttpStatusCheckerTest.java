package org.example.home;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class HttpStatusCheckerTest {

    @Test
    void getStatusImageSuccessTest() throws Exception {

        HttpStatusChecker statusChecker = new HttpStatusChecker();
        String result = statusChecker.getStatusImage(200);

        assertEquals("https://http.cat/200.jpg", result);
    }

    @Test
    void getStatusImageFailTest() throws Exception {

        HttpStatusChecker statusChecker = new HttpStatusChecker();
        assertThrows(FileNotFoundException.class,
                () -> statusChecker.getStatusImage(-999999999));
    }
}