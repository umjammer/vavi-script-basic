
package com.github.shakeo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

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
}
