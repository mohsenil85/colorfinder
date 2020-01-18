package com.lmohseni;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AppTest {

    App app = new App();

    @Test
    public void main() throws IOException {
        App.main(new String[]{"args"});
    }
}
