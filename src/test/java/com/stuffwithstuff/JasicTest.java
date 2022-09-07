/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.stuffwithstuff;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import vavi.util.Debug;


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

    @Disabled("wip")
    @Test
    void testJsr223() throws Exception {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("jasic");
        Debug.println("engine: " + engine);

        String statement = "test = 100";
        Object result = engine.eval(statement);
Debug.println("result: " + result);

        statement = "print test + 100";
        result = engine.eval(statement);
Debug.println("result: " + result);
    }
}

/* */
