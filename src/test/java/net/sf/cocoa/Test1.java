/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package net.sf.cocoa;

import java.io.InputStream;
import java.io.OutputStream;

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
}

/* */
