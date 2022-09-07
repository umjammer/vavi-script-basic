/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.EOFException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import vavi.util.Debug;


/**
 * Main1.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/03/02 umjammer initial version <br>
 */
public class Main2 {

    @Test
    void test1() throws Exception {
        main(new String[] {});
    }

    @Test
    void test3() throws Exception {
        IntStream.range(0, NBasicBin2Text2.keywordsBase.length).forEach(i ->
                System.err.printf("%02X=%s%n", 0x81 + i, NBasicBin2Text2.keywordsBase[i]));
        IntStream.range(0, NBasicBin2Text2.keywordsFF.length).forEach(i ->
                System.err.printf("FF_%02X=%s%n", 0x81 + i, NBasicBin2Text2.keywordsFF[i]));
    }

    @Test
    void test2() throws Exception {
        Test1.main(new String[] {"src/test/resources/grph3d.bas"});
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Path p = Paths.get(Main2.class.getResource("/grph3d.bas").toURI());

        NBasicBin2Text2 covetrer = new NBasicBin2Text2();
        try {
            covetrer.convert(Files.newInputStream(p), System.out);
        } catch (EOFException e) {
Debug.println("Done");
        }
    }
}

/* */
