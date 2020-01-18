package com.lmohseni;

import org.junit.Test;

import java.io.IOException;

public class AppTest {

    App app = new App();

    @Test
    public void main() throws IOException {
        App.main(new String[]{"args"});
    }
}
