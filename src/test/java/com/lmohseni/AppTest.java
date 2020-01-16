package com.lmohseni;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class AppTest {

    App app = new App();

    @Test(expected = IOException.class)
    public void downloadImageInvalid() throws IOException {
        app.downloadImage("invalidHttp");
    }

    @Test(expected = IOException.class)
    public void downloadImageNonexistant() throws IOException {
        app.downloadImage("http://foo.example");
    }

}
