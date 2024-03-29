/*
 * BASIC.java -  BASIC Interpreter in Java.
 *
 * Copyright (c) 1996 Chuck McManis, All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies.
 *
 * CHUCK MCMANIS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. CHUCK MCMANIS
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT
 * OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package net.sf.cocoa.basic;

import java.io.IOException;

import net.sf.cocoa.dlib.ConsoleWindow;

public class Basic {

    public static void main(String args[]) {

        ConsoleWindow cw = new ConsoleWindow("Java BASIC 1.0");

        CommandInterpreter ci = new CommandInterpreter(cw.dataInputStream(),
                                    cw.printStream());
        try {
            ci.start();
        } catch (Exception e) {
            System.out.println("Caught an Exception:");
            e.printStackTrace();
            try {
                System.out.println("Press enter to continue.");
                System.in.read();
            } catch (IOException xx) { }
        }
    }
}
