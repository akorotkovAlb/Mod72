package org.example.home;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class HttpStatusImageDownloaderTest {

    @Test
    void downloadStatusImageTest() throws Exception {

        HttpStatusImageDownloader downloader = new HttpStatusImageDownloader();
        downloader.downloadStatusImage(418);

        File image = new File("cats/418.jpg");
        assertTrue(image.exists());
    }
}