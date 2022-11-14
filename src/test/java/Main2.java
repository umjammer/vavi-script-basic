/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.EOFException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vavi.util.Debug;


/**
 * Main2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/03/02 umjammer initial version <br>
 */
public class Main2 {

    @Test
    @DisplayName("convert n-basic")
    void test1() throws Exception {
        main(new String[] {});
    }

    @Test
    @Disabled("create properties for n-basic")
    void test3() throws Exception {
        IntStream.range(0, NBasicBin2Ascii.keywordsBase.length).forEach(i ->
                System.err.printf("%02X=%s%n", 0x81 + i, NBasicBin2Ascii.keywordsBase[i]));
        IntStream.range(0, NBasicBin2Ascii.keywordsFF.length).forEach(i ->
                System.err.printf("FF_%02X=%s%n", 0x81 + i, NBasicBin2Ascii.keywordsFF[i]));
    }

    @Test
    @DisplayName("convert n88-basic")
    void test2() throws Exception {
        N88BasicBin2Ascii.main(new String[] {"src/test/resources/grph3d.bas"});
    }

    /**
     * run n-basic converter
     */
    public static void main(String[] args) throws Exception {
        Path p = Paths.get(Main2.class.getResource("/grph3d.bas").toURI());

        NBasicBin2Ascii covetrer = new NBasicBin2Ascii();
        try {
            covetrer.convert(Files.newInputStream(p), System.out);
        } catch (EOFException e) {
Debug.println("Done");
        }
    }
}

/* */
