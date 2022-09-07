/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package net.sf.cocoa;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import net.sf.cocoa.jsr223.BasicScriptEngineFactory;
import org.junit.jupiter.api.Test;

import vavi.util.Debug;

import net.sf.cocoa.basic.CommandInterpreter;


/**
 * Test1.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/03/03 umjammer initial version <br>
 */
class Test1 {

    @Test
    void test1() throws Exception {
        InputStream is = Test1.class.getResourceAsStream("test1.bas");
        OutputStream os = System.out;

        CommandInterpreter ci = new CommandInterpreter(is, os);
        ci.start(true);
Debug.println("done");
    }

    @Test
    void test2() throws Exception {
        InputStream is = Test1.class.getResourceAsStream("test2.bas");
        OutputStream os = System.out;

        CommandInterpreter ci = new CommandInterpreter(is, os);
        ci.start(true);
Debug.println("done");
    }

    @Test
    public void test0() throws Exception {
        ScriptEngineManager sem = new ScriptEngineManager();
        List<ScriptEngineFactory> list = sem.getEngineFactories();

        for (int i = 0; i < list.size(); i++) {
            ScriptEngineFactory f = list.get(i);

            String engineName = f.getEngineName();
            String engineVersion = f.getEngineVersion();
            String langName = f.getLanguageName();
            String langVersion = f.getLanguageVersion();
            System.out.println("\n---- " + i + " ----\n" + engineName + " " +
                    engineVersion + " (" +
                    langName + " " +
                    langVersion + ")");
        }
    }

    @Test
    void testJsr223() throws Exception {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("basic");
Debug.println("engine: " + engine);

        String statement =
                "100 FOR i = 1 to 10\n" +
                "110 FOR j = i to 10\n" +
                "120 PRINT \"*\";\n" +
                "130 NEXT j\n" +
                "140 NEXT i";
        Object result = engine.eval(statement);
Debug.println("result: " + result);

        statement = "100 LET A = 1 + 100";
        result = engine.eval(statement);
Debug.println("result: " + result);
    }
}

/* */
