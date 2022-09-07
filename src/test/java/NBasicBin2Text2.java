/*
 * https://github.com/autumn009/NBasicBin2Text
 */

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;


public class NBasicBin2Text2 {

    static final String[] keywordsBase = {
            "END", "FOR", "NEXT", "DATA", "INPUT", "DIM", "READ", "LET", "GOTO", "RUN", "IF", "RESTORE", "GOSUB", "RETURN", "REM",

            "STOP", "PRINT", "CLEAR", "LIST", "NEW", "ON", "WAIT", "DEF", "POKE", "CONT", "CSAVE", "CLOAD", "OUT", "LPRINT",
            "LLIST", "CONSOLE",

            "WIDTH", "ELSE", "TRON", "TROFF", "SWAP", "ERASE", "ERROR", "RESUME", "DELETE", "AUTO", "RENUM", "DEFSTR", "DEFINT",
            "DEFSNG", "DEFDBL", "LINE",

            "PRESET", "PSET", "BEEP", "FORMAT", "KEY", "COLOR", "TERM", "MON", "CMD", "MOTOR", "POLL", "RBYTE", "WBYTE", "ISET",
            "IRESET", "TALK",

            "MAT", "LISTEN", "DSKO$", "REMOVE", "MOUNT", "OPEN", "FIELD", "GET", "PUT", "SET", "CLOSE", "LOAD", "MERGE", "FILES",
            "NAME", "KILL",

            "LSET", "RSET", "SAVE", "LFILES", "INIT", "LOCATE", "???[0xd6]???", "TO", "THEN", "TAB(", "STEP", "USR", "FN", "SPC(",
            "NOT", "ERL",

            "ERR", "STRING$", "USING", "INSTR", ",", "VARPTR", "CSRLIN", "ATTR$", "DSKI$", "INKEY$", "TIME$", "DATE$",
            "???[0xec]???", "SQR", "STATUS", "POINT",

            ">", "=", "<", "+", "-", "*", "/", "^", "AND", "OR", "XOR", "EQV", "IMP", "MOD", "\\"
    };

    static final String[] keywordsFF = {
            "LEFT$", "RIGHT$", "MID$", "SGN", "INT", "ABS", "SQR", "RND", "SIN", "LOG", "EXP", "COS", "TAN", "ATN", "FRE", "INP",
            "POS", "LEN", "STR$", "VAL", "ASC", "CHR$", "PEEK", "SPACE$", "OCT$", "HEX$", "LPOS", "PORT", "DEC", "BCD$", "CINT",
            "CSNG", "CDBL", "FIX", "CVI", "CVS", "CVD", "DSKF", "EOF", "LOC", "LOF", "FPOS", "MKI$", "MKS$", "MKD$"
    };

    private static void usage() {
        System.err.println("N-BASIC Binary to Text converter Version 2.0");
        System.err.println("usage: java NBasic2Text [-p] [-e] [-g] [-l] INPUTFILE [OUTPUTFILE]");
    }

    boolean bPretty = false;
    boolean bGraphWarn = false;
    boolean bLastEOF = false;
    String extraSpace = " ";

    public static void main(String[] args) throws IOException {

        NBasicBin2Text2 app = new NBasicBin2Text2();

        String srcFileName = null;
        String dstFileName = null;

        for (String item : args) {
            if ((item.equalsIgnoreCase("-p") ? 0 : 1) == 0)
                app.bPretty = true;
            else if ((item.equalsIgnoreCase("-e") ? 0 : 1) == 0)
                app.extraSpace = " ";
            else if ((item.equalsIgnoreCase("-g") ? 0 : 1) == 0)
                app.bGraphWarn = true;
            else if ((item.equalsIgnoreCase("-l") ? 0 : 1) == 0)
                app.bLastEOF = true;
            else if (srcFileName == null)
                srcFileName = item;
            else
                dstFileName = item;
        }
        if (srcFileName == null) {
            usage();
            return;
        }

        try {
            app.convert(Files.newInputStream(Paths.get(srcFileName)),
                    dstFileName != null ?
                            Files.newOutputStream(Paths.get(dstFileName)) : System.out);
        } catch (EOFException e) {
            System.err.println("Encountered unexpected EOF");
        }
    }

    public void convert(InputStream in, OutputStream out) throws IOException {
        try (DataInputStream dis = new DataInputStream(in);
             PrintWriter dos = new PrintWriter(out)) {

            dis.readUnsignedByte();
            int o = dis.readUnsignedByte() * 0x100;

            while (true) {
                // get line header
                int linkLow;
                linkLow = dis.readUnsignedByte();
                int linkHigh = dis.readUnsignedByte();
                if (linkLow == 0 && linkHigh == 0)
                    break;

                int lineNumberLow = dis.readUnsignedByte();
                int lineNumberHigh = dis.readUnsignedByte();
                int lineNumber = lineNumberLow + (lineNumberHigh << 8);
                dos.print(String.format("%d ", lineNumber));

                boolean remOrDataMode = false;
                boolean quoteMode = false;
                // process a line
                while (true) {
                    int ch = dis.readUnsignedByte();
                    ch = ch & 0xff; // must be unsigned value
                    if (ch == 0) { // end of line
                        dos.println();
                        break;
                    } else if (ch == 0x0b) { // octal constant
                        int vl = dis.readUnsignedByte();
                        int vh = dis.readUnsignedByte();
                        int val = (vl & 0xff) + ((vh & 0xff) << 8);
                        dos.print("&O");
                        dos.print(String.format("%o", val));
                    } else if (ch == 0x0c) { // hexa constant
                        int vl = dis.readUnsignedByte();
                        int vh = dis.readUnsignedByte();
                        int val = (vl & 0xff) + ((vh & 0xff) << 8);
                        dos.print("&H");
                        dos.print(String.format("%x", val));
                    } else if (ch == 0x0d) { // absolute address replaced
                        // from line number
                        System.err.println("Warning: Encountered Absolute Address (0x0d)");
                        dis.readUnsignedByte();
                        dis.readUnsignedByte();
                    } else if (ch == 0x0e) { // line number
                        int vl = dis.readUnsignedByte();
                        int vh = dis.readUnsignedByte();
                        int val = (vl & 0xff) + ((vh & 0xff) << 8);
                        dos.print(val);
                    } else if (ch == 0x0f) { // single byte constant
                        int val = dis.readUnsignedByte();
                        dos.print(val);
                    } else if (ch >= 0x11 && ch <= 0x1a) { // one digit                                                               // constant
                        int val = ch - 0x11;
                        dos.print(val);
                    } else if (ch == 0x1c) { // two byte integer
                        int vl = dis.readUnsignedByte();
                        int vh = dis.readUnsignedByte();
                        int val = (vl & 0xff) + ((vh & 0xff) << 8);
                        if ((val & 0x8000) != 0) {
                            val |= 0xffff0000;
                        }
                        dos.print(val);
                    } else if (ch == 0x1d) { // four byte float
                        int v1 = dis.readUnsignedByte();
                        int v2 = dis.readUnsignedByte();
                        int v3 = dis.readUnsignedByte();
                        int v4 = dis.readUnsignedByte();
                        int kasu = (v1 & 0xff) + ((v2 & 0xff) << 8) + ((v3 & 0x7f) << 16);
                        assert (v3 & 0x80) == 0;
                        int sisu = v4 - 0x81;
                        float r = 1;
                        float d = 0.5f;
                        int mask = 0x400000;
                        for (int i = 0; i < 23; i++) {
                            if ((kasu & mask) != 0) {
                                r = r + d;
                            }
                            mask >>= 1;
                            d /= 2.0f;
                        }
                        if (sisu == 0) {
                            r = (float) 0.0;
                        }
                        if (sisu < 0) {
                            for (int i = 0; i < Math.abs(sisu); i++) {
                                r /= 2.0f;
                            }
                        } else {
                            for (int i = 0; i < sisu; i++) {
                                r *= 2.0f;
                            }
                        }
                        dos.print(r);
                    } else if (ch == 0x1f) { // eight byte float
                        int v1 = dis.readUnsignedByte();
                        int v2 = dis.readUnsignedByte();
                        int v3 = dis.readUnsignedByte();
                        int v4 = dis.readUnsignedByte();
                        int v5 = dis.readUnsignedByte();
                        int v6 = dis.readUnsignedByte();
                        int v7 = dis.readUnsignedByte();
                        int v8 = dis.readUnsignedByte();
                        int kasu1 = (v1 & 0xff) + ((v2 & 0xff) << 8) + ((v3 & 0xff) << 16);
                        int kasu2 = (v4 & 0xff) + ((v5 & 0xff) << 8) + ((v6 & 0xff) << 16) + ((v7 & 0x7f) << 24);
                        assert (v7 & 0x80) == 0;
                        int sisu = v8 - 0x81;
                        double r = 1;
                        double d = 0.5;
                        int mask2 = 0x40000000;
                        for (int i = 0; i < 31; i++) {
                            if ((kasu2 & mask2) != 0) {
                                r = r + d;
                            }
                            mask2 >>= 1;
                            d /= 2.0;
                        }
                        int mask1 = 0x800000;
                        for (int i = 0; i < 24; i++) {
                            if ((kasu1 & mask1) != 0) {
                                r = r + d;
                            }
                            mask1 >>= 1;
                            d /= 2.0;
                        }
                        if (sisu == 0) {
                            r = 0.0;
                        }
                        if (sisu < 0) {
                            for (int i = 0; i < Math.abs(sisu); i++) {
                                r /= 2.0;
                            }
                        } else {
                            for (int i = 0; i < sisu; i++) {
                                r *= 2.0;
                            }
                        }
                        String s = String.valueOf(r);
                        if (s.contains("E"))
                            s = s.replace("E", "D");
                        else
                            s = s + "#";
                        dos.print(s);
                    } else if (ch >= 0x81 && ch <= 0xfe && !quoteMode && !remOrDataMode) {
                        dos.print(String.format(extraSpace + "%s" + extraSpace, keywordsBase[ch - 0x81]));
                        if (ch == 0x8f) { // REM
                            remOrDataMode = true;
                        }
                        if (ch == 0x84) { // DATA
                            remOrDataMode = true;
                        }
                    } else if (ch == 0xff && !quoteMode && !remOrDataMode) {
                        int ch2 = dis.readUnsignedByte();
                        int ch2Munus81 = ch2 - 0x81;
                        if (ch2Munus81 >= 0 && ch2Munus81 < keywordsFF.length) {
                            dos.print(String.format(extraSpace + "%s" + extraSpace, keywordsFF[ch2Munus81]));
                        } else if (ch2 == 0xec) {
                            dos.print(String.format(extraSpace + "IEEE" + extraSpace));
                        } else {
                            dos.print(String.format("???[0x%02x][0x%02x]???", ch, ch2));
                        }
                    } else if (ch >= 0x20 && ch <= 0x7e) {
                        if (ch == '"') {
                            quoteMode = !quoteMode;
                            dos.print((char) ch);
                        } else if (!quoteMode && ch == ':') {
                            remOrDataMode = false;
                            if (bPretty) {
                                dos.println();
                                dos.print("\t");
                            } else {
                                dos.print((char) ch);
                            }
                        } else {
                            dos.print((char) ch);
                        }
                    } else if (bGraphWarn && (ch < 0xa1 || ch > 0xdf))
                        dos.print(String.format("???[0x%02x]???", ch));
                    else
                        dos.print((char) ch);
                }
            }
        } finally {
            if (bLastEOF)
                out.write(0x1a);
        }
    }
}
