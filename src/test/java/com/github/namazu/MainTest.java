/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.namazu;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

import com.github.namazu.lexical_parser.Token;
import com.github.namazu.lexical_parser.TokenParser;

class MainTest {

    @Test
    void test() throws Exception {
        Main.main(new String[] { "src/test/resources/com/github/namazu/test1.bas" });
    }

    @Test
    void test2() throws Exception {
        Main.main(new String[] { "src/test/resources/com/github/namazu/test2.bas" });
    }

    @Test
    void tokenParserTest() throws Exception {
        InputStream is = MainTest.class.getResourceAsStream("test1.bas");
        TokenParser parser = new TokenParser(new InputStreamReader(is));

        Token token = parser.get();
        while (token != null) {
            System.err.println(token);
            token = parser.get();
        }
    }

    @Test
    void tokenParserTest2() throws Exception {
        InputStream is = MainTest.class.getResourceAsStream("test2.bas");
        TokenParser parser = new TokenParser(new InputStreamReader(is));

        Token token = parser.get();
        while (token != null) {
            System.err.println(token);
            token = parser.get();
        }
    }
}

/* */
