/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.stuffwithstuff;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 * JasicTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/03/03 umjammer initial version <br>
 */
class JasicTest {

    @Test
    void test() throws Exception {
        Path p = Paths.get(JasicTest.class.getResource("hello.jas").toURI());
        String contents = new String(Files.readAllBytes(p));
        Jasic jasic = new Jasic();
        jasic.interpret(contents);
    }

    @Test
    void test1() throws Exception {
        Path p = Paths.get(JasicTest.class.getResource("hellos.jas").toURI());
        String contents = new String(Files.readAllBytes(p));
        Jasic jasic = new Jasic();
        jasic.interpret(contents);
    }

    @Test
    void test2() throws Exception {
        Path p = Paths.get(JasicTest.class.getResource("mandel.jas").toURI());
        String contents = new String(Files.readAllBytes(p));
        Jasic jasic = new Jasic();
        jasic.interpret(contents);
    }
}

/* */
