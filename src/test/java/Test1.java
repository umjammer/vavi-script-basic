/*
 * https://github.com/mumriks/b2an88b/blob/main/b2an88b.rb
 */

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import vavi.io.LittleEndianDataInputStream;
import vavi.util.Debug;


/**
 * N88-BASIC 中間コードファイルをアスキーに変換
 * <p>
 * TODO space between keyword and keyword
 *
 * @author @kishi24
 * @version 2022/06/10 ver.1.6.1
 */
public class Test1 {

    static int offset = 0;

    static final Properties data = new Properties();

    static {
        try {
            // TODO we need to change table for n80, n88, n88(86)
            data.load(Test1.class.getResourceAsStream("/n98.properties"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    static String read_4byte(LittleEndianDataInputStream file) throws IOException {
        int one = file.readUnsignedByte();
        int two = file.readUnsignedByte();
        int three = file.readUnsignedByte();
        int data = file.readUnsignedByte();

        if (0 == data) {
            return "0";
        }

        // 仮数部の処理
        String kasu = String.format("%02x%02x%02x", three, two, one);
        String frac = Integer.toBinaryString(Integer.parseInt(kasu, 16));

        // to_s(2) による桁落ちを補正
        if (24 > frac.length()) {
            int m = 24 - frac.length();
            for (int i = 0; i < m; i++) {
                frac = "0" + frac;
            }
        }
//Debug.println(frac + ", " + frac.length() + ", " + kasu + ", " + e);

        int e = data - 129;
        boolean sign = false;
        if ('1' == frac.charAt(0)) {
            sign = true;
        } else {
            frac = '1' + frac.substring(1);
        }

        // 0b から実数へ変換
        double num = 0.0;
        for (int i = 0; i <= 22; i++) {
            if ('1' == frac.charAt(i)) {
                num += 1 / Math.pow(2.0, i);
            }
        }
        num = num * Math.pow(2, e);

        // 有効桁 7 におさめて行末の 0 を削除
        String[] data2 = String.valueOf(num).split("\\.");
        if (data2[1].matches("^0+$")) {
            return (sign ? "-" : "") + data2[0] + "!";
        }
        int nth;
        if ("0".equals(data2[0])) {
            nth = 7 - data2[0].length();
        } else {
            nth = 6 - data2[0].length();
        }
        String num2 = BigDecimal.valueOf(num).round(new MathContext(nth, RoundingMode.HALF_UP)).toString();

        num2 = num2.replaceFirst("0+$", "");
        num2 = num2.replaceFirst("\\.$", "");
        num2 = num2.replaceFirst("^0\\.", "\\.");
        if (sign) {
            num2 = "-" + num2;
        }
        return num2;
    }

    static String read_8byte(LittleEndianDataInputStream file) throws IOException {
        int one = file.readUnsignedByte();
        int two = file.readUnsignedByte();
        int three = file.readUnsignedByte();
        int four = file.readUnsignedByte();
        int five = file.readUnsignedByte();
        int six = file.readUnsignedByte();
        int seven = file.readUnsignedByte();
        int data = file.readUnsignedByte();
        if (0 == data) {
            return "0";
        }
        String kasu = String.format("%02x%02x%02x%02x%02x%02x%02x", seven, six, five, four, three, two, one);
        String frac = Long.toBinaryString(Long.parseLong(kasu, 16));

        if (56 > frac.length()) {
            int m = 56 - frac.length();
            for (int i = 0; i < m; i++) {
                frac = "0" + frac;
            }
        }

        int e = data - 129;
        boolean sign = false;
        if ('1' == frac.charAt(0)) {
            sign = true;
        } else {
            frac = '1' + frac.substring(1);
        }

        double num = 0.0;
        for (int i = 0; i <= 55; i++) {
            if ('1' == frac.charAt(i)) {
                num += 1 / Math.pow(2.0, i);
            }
        }
        num = num * Math.pow(2, e);

        String[] data2 = String.valueOf(num).split("\\.");
        int nth;
        if ("0".equals(data2[0])) {
            nth = 17 - data2[0].length();
        } else {
            nth = 16 - data2[0].length();
        }
        String num2 = BigDecimal.valueOf(num).round(new MathContext(nth, RoundingMode.HALF_UP)).toString();

        num2 = num2.replaceFirst("0+$", "");
        num2 = num2.replaceFirst("\\.$", "");
        num2 = num2.replaceFirst("^0\\.", "\\.");
        if (sign)
            num2 = "-" + num2;
        return num2;
    }

    public static void main(String[] args) throws IOException {

        String file = args[0];
        LittleEndianDataInputStream f = new LittleEndianDataInputStream(Files.newInputStream(Paths.get(file)));

        int h = f.readUnsignedByte();
        if (h == 0xfe) {
Debug.printf("MS-DOS version");
        }

        while (f.available() > 1) {
            int pt = f.readUnsignedShort();
            if (pt == 0) {
                break;
            }

            boolean text = false;
            int ifthen = 0;
            int label = 0;

            int p_num = pt;
            int line_num = f.readUnsignedShort();
            System.out.printf("%d ", line_num);
            p_num -= 4;
            String remark = "";

            while (f.available() > 0 && p_num > 0) {
                int dump = f.readUnsignedByte();
                p_num -= 1;
                if (0 < dump && dump < 0xa) {
                    if (label == 0) {
                        System.out.printf(String.format("%%-%ds", dump), "");
                    } else if (label == -1) {
                        label = dump;
                    }
                } else if (0x0A == dump) {
                    // LF
                    System.out.print("\r");
                } else if (0x0B == dump) {
                    // 2 byte Octet
                    int fig = f.readUnsignedShort();
                    System.out.printf("&O%o", fig);
                    p_num -= 2;
                } else if (0x0C == dump) {
                    // 2 byte Hexa
                    int fig = f.readUnsignedShort();
                    System.out.printf("&H%X", fig);
                    p_num -= 2;
                } else if (0x0D == dump) {
                    // 2 byte address
                    int fig = f.readUnsignedShort();
                    System.out.printf("%XH", fig);
                    p_num -= 2;
                } else if (0x0E == dump) {
                    // 2 byte integer
                    int fig = f.readUnsignedShort();
                    System.out.print(fig);
                    p_num -= 2;
                } else if (0x0F == dump) {
                    // 1 byte integer
                    int fig = f.readUnsignedByte();
                    System.out.print(fig);
                    p_num -= 1;
                } else if (0x10 <= dump && dump <= 0x19) {
                    // 1 byte integer
                    System.out.printf("%d", dump - 0x10);
                } else if (0x1a == dump) {
                    System.err.println("SYNTAX ERROR: 0x1a");
                } else if (0x1B == dump) {
                    // kanji in/out
                } else if (0x1C == dump) {
                    int fig = f.readShort();
                    System.out.print(fig);
                    p_num -= 2;
                } else if (0x1D == dump) {
                    System.out.print(read_4byte(f));
                    p_num -= 4;
                } else if (0x1F == dump) {
                    System.out.printf("%s#", read_8byte(f));
                    p_num -= 8;
                } else if (0x1f < dump && dump < 0x80) {
                    if (0x3A != dump) {
                        if (":".equals(remark)) {
                            System.out.print(remark);
                            remark = "";
                        }
                        System.out.printf("%c", dump);
                        if (0x41 <= dump && dump <= 0x5A) {
                            if (label == 0) {
                                label = -1;
                            }
                        }
                        if (0x31 <= dump && dump <= 0x39 || 0x41 <= dump && dump <= 0x5A) {
                            if (label != 0) {
                                label--;
                            }
                        }
                        if (0x22 == dump) {
                            text = !text;
                        }
                        if (0x27 == dump) {
                            text = true; // ';
                        }
                    } else if (1 == ifthen && 0x3A == dump) {
                        ifthen = 2;
                        if (remark.isEmpty()) {
                            remark += (char) dump;
                        } else {
                            System.out.print(remark);
                            remark = "";
                        }
                    } else {
                        if (remark.isEmpty()) {
                            remark += (char) dump;
                        } else {
                            System.out.print(remark);
                        }
                    }
                } else if (0x7f < dump && dump < 0xff) {
                    if (!text) {
                        if (":".equals(remark) && 0xff == dump) { // REM TODO (88:0x8F)
                            remark += (char) dump;
                        } else if (":\u00ff".equals(remark) && 0xff == dump) { // :REM' TODO (88:0xE9)
                            System.out.print(data.get(String.format("%02X", dump)));
                            remark = "";
                            text = true;
                        } else if (0xa8 == dump) { // if TODO (88:0x8B)
                            ifthen = 1;
                            System.out.print(data.get(String.format("%02X", dump)));
                        } else if (":".equals(remark) && 0x99 == dump) { // TODO (88:0x9F)
                            remark = "";
                            System.out.print(data.get(String.format("%02X", dump)));
                        } else {
                            if (":".equals(remark)) {
                                System.out.print(":");
                                remark = "";
                            }
                            System.out.print(data.get(String.format("%02X", dump)));
                            if (0x8F == dump) {
                                text = true; // REM
                            }
                            if (0x84 == dump) {
                                text = true; // DATA
                            }
                        }
                    } else {
                        if (":".equals(remark) && 0x8F == dump) { // REM
                            remark += (char) dump;
                        } else if (":\u008F".equals(remark) && 0xE9 == dump) { // :REM'
                            System.out.print(data.get(String.format("%02X", dump)));
                            remark = "";
                            text = true;
                        } else {
                            if (":".equals(remark)) {
                                System.out.print(":");
                                remark = "";
                            }
                            System.out.print(data.get(String.format("%02X", dump)));
                        }
                    }
                } else if (0xFF == dump) {
                    int fig = f.readUnsignedByte();
                    System.out.print(data.get(String.format("FF_%02X", fig)));
                    p_num -= 1;
                } else if (0x00 == dump) {
                    if (!remark.isEmpty()) {
                        System.out.print(remark);
                    }
                    remark = "";
                }
            }
            System.out.println();
            text = false;
            ifthen = 0;
        }
    }
}
