
package com.github.shakeo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import vavi.util.Debug;


public class Test1 {

    @Test
    void test() throws Exception {
        Path p = Paths.get(Test1.class.getResource("test1.bas").toURI());
        BasicMachine machine = new BasicMachine(System.out::print);
        Files.readAllLines(p).forEach(machine::execute);
    }

    @Test
    void test2() throws Exception {
        Path p = Paths.get(Test1.class.getResource("test2.bas").toURI());
        BasicMachine machine = new BasicMachine(System.out::print);
        Files.readAllLines(p).forEach(machine::execute);
    }

    /**
     * test driver
     */
    @Test
    void test3() throws Exception {
        BasicLexer b = new BasicLexer("PRINT \"AA A\"/140433+B");
        int s;
        while((s = b.getToken()) != BasicLexer.END){
            Debug.println(s);
            Debug.println("Token = " + (
                s == BasicLexer.NAME ? b.getStringValue() :
                s == BasicLexer.STRING ? b.getStringValue() :
                s == BasicLexer.NUMBER ? b.numberIsReal() ? b.getRealValue() : b.getIntegerValue() :
                s == BasicLexer.OPERATOR ? b.getStringValue() :
                s == BasicLexer.END ? "" : ""));
        }
    }
}
