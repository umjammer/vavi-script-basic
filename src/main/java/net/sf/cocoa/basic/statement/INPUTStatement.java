/*
 * INPUTStatement.java - Implement the INPUT Statement.
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

package net.sf.cocoa.basic.statement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.cocoa.basic.BasicRuntimeError;
import net.sf.cocoa.basic.BasicSyntaxError;
import net.sf.cocoa.basic.LexicalTokenizer;
import net.sf.cocoa.basic.Program;
import net.sf.cocoa.basic.Statement;
import net.sf.cocoa.basic.Token;
import net.sf.cocoa.basic.Variable;

/**
 * This is the INPUT statement.
 *
 * The syntax of the INPUT statement is :
 *      INPUT [ "prompt"; ] var1, var2, ... varN
 *
 * The variables can be string variables or numeric variables but they
 * cannot be expressions. When reading into a string variable all characters
 * up to the first comma are stored into the string variable, unless the
 * string is quoted with " characters.
 *
 * If insufficient input is provided, the prompt is re-iterated for more data.
 * Syntax errors:
 *      Semi-colon expected after the prompt string.
 *      Malformed INPUT statement.
 * Runtime errors:
 *      Type mismatch.
 *
 */
public class INPUTStatement extends Statement {

    /** The prompt is displayed prior to requesting input. */
    String prompt;

    /** This vector holds a list of variables to fill */
    List<Token> args;

    /**
     * Construct a new INPUT statement object.
     */
    public INPUTStatement(LexicalTokenizer lt) throws BasicSyntaxError {
        super(INPUT);
        parse(this, lt);
    }

    /**
     * Execute the INPUT statement. Most of the work is done in fillArgs.
     */
    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BasicRuntimeError {
        BufferedReader dis = new BufferedReader(new InputStreamReader(in));
        getMoreData(dis, out, prompt);
        fillArgs(dis, out, prompt, pgm, args);
        return (pgm.nextStatement(this));
    }

    /**
     * Reconstruct this statement from its parsed data.
     */
    public String unparse() {
        StringBuilder sb = new StringBuilder();
        sb.append("INPUT ");
        if (prompt != null) {
            sb.append("\"").append(prompt).append("\"; ");
        }
        for (int i = 0; i < args.size(); i++) {
            Variable va = (Variable) args.get(i);
            if (i < (args.size() - 1)) {
                sb.append(va.unparse()).append(", ");
            } else {
                sb.append(va.unparse());
            }
        }
        return sb.toString();
    }

    /**
     * This is our buffer for processing INPUT statement requests.
     */
    private int currentPos= 500;
    private final char[] buffer = new char[256];

    void getMoreData(BufferedReader in, PrintStream out, String prompt) throws BasicRuntimeError {
        String x = null;

        if (prompt != null) {
            out.print(prompt);
        } else {
            out.print("?");
        }
        out.print(" ");
        out.flush();

        try {
            x = in.readLine();
        } catch (IOException ioe) {
            throw new BasicRuntimeError(this, "I/O error on input.");
        }
        if (x == null)
            throw new BasicRuntimeError(this, "Out of data for INPUT.");
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (x.length() > i) ? x.charAt(i) : 0;
        }
        buffer[x.length()] = '\n';
        currentPos = 0;
    }

    /*
     * Read a floating point number from the character buffer array.
     */
    double getNumber(BufferedReader in, PrintStream out, String prompt) throws BasicRuntimeError {
        double m = 0;   // Mantissa
        double f = 0;   // Fractional component
        int oldPos = currentPos; // save our place.
        boolean wasNeg = false;

        if (currentPos >= buffer.length)
            getMoreData(in, out, prompt);

        while (Character.isWhitespace(buffer[currentPos])) {
            if (buffer[currentPos] == '\n') {
                getMoreData(in, out, prompt);
            }
            currentPos++;
            if (currentPos >= buffer.length)
                getMoreData(in, out, prompt);
        }

        if (buffer[currentPos] == '-') {
            wasNeg = true;
            currentPos++;
        }

        // Look for the integral part.
        while (Character.isDigit(buffer[currentPos])) {
            m = (m*10.0) + (buffer[currentPos++] - '0');
        }

        // Now look for the fractional part.
        if (buffer[currentPos] == '.') {
            currentPos++;
            double t = .1;
            while (Character.isDigit(buffer[currentPos])) {
                f = f + (t * (buffer[currentPos++] - '0'));
                t = t/10.0;
            }
        } else if (currentPos == oldPos) // no number found
            throw new BasicRuntimeError(this, "Number expected.");

        m = (m + f) * ((wasNeg) ? -1 : 1);
        // so it was a number, perhaps we are done with it.
        if ((buffer[currentPos] != 'E') && (buffer[currentPos] != 'e')) {
            return m;
        }

        currentPos++; // skip over the 'e'

        int p = 0;
        double e;
        wasNeg = false;

        // check for negative exponent.
        if (buffer[currentPos] == '-') {
            wasNeg = true;
            currentPos++;
        } else if (buffer[currentPos] == '+') {
            currentPos++;
        }

        while (Character.isDigit(buffer[currentPos])) {
            p = (p * 10) + (buffer[currentPos++] - '0');
        }

        try {
            e = Math.pow(10, (double)p);
        } catch (ArithmeticException zzz) {
            throw new BasicRuntimeError(this, "Illegal numeric constant.");
        }

        if (wasNeg)
            e = 1/e;
        return m * e;
    }

    String getString(BufferedReader in, PrintStream out, String prompt) throws BasicRuntimeError {
        StringBuilder sb = new StringBuilder();

        if (currentPos >= buffer.length)
            getMoreData(in, out, prompt);

        while (Character.isWhitespace(buffer[currentPos])) {
            if (buffer[currentPos] == '\n') {
                getMoreData(in, out, prompt);
            }
            currentPos++;
            if (currentPos >= buffer.length)
                getMoreData(in, out, prompt);
        }

        boolean inQuote = false;
        while (true) {
            switch((int) buffer[currentPos]) {
                case '\n':
                    return (sb.toString()).trim();
                case '"' :
                    if (buffer[currentPos+1] == '"') {
                        currentPos++;
                        sb.append('"');
                    } else if (inQuote) {
                        currentPos++;
                        return sb.toString();
                    } else {
                        inQuote = true;
                    }
                    break;
                case ',' :
                    if (inQuote) {
                        sb.append(',');
                    } else {
                        return (sb.toString()).trim();
                    }
                    break;
                default :
                    sb.append(buffer[currentPos]);
            }
            currentPos++;
            if (currentPos >= buffer.length)
                return sb.toString();
        }
    }

    void fillArgs(BufferedReader in, PrintStream out, String prompt, Program pgm, List<Token> v) throws BasicRuntimeError {
        for (Token token : v) {
            Variable vi = (Variable) token;
            if (buffer[currentPos] == '\n')
                getMoreData(in, out, "(more)" + ((prompt == null) ? "?" : prompt));
            if (!vi.isString()) {
                pgm.setVariable(vi, getNumber(in, out, prompt));
            } else {
                pgm.setVariable(vi, getString(in, out, prompt));
            }
            while (true) {
                if (buffer[currentPos] == ',') {
                    currentPos++;
                    break;
                }

                if (buffer[currentPos] == '\n') {
                    break;
                }

                if (Character.isWhitespace(buffer[currentPos])) {
                    currentPos++;
                    continue;
                }
                throw new BasicRuntimeError(this, "Comma expected, got '" + buffer[currentPos] + "'.");
            }
        }
    }

    /**
     * Parse INPUT Statement.
     */
    private static void parse(INPUTStatement s, LexicalTokenizer lt) throws BasicSyntaxError {
        Token t;
        boolean needComma = false;
        s.args = new ArrayList<>();

        // get optional prompt string.
        t = lt.nextToken();
        if (t.typeNum() == Token.STRING) {
            s.prompt = t.stringValue();
            t = lt.nextToken();
            if (! t.isSymbol(';'))
                throw new BasicSyntaxError("semi-colon expected after prompt string.");
        } else {
            lt.unGetToken();
        }
        while (true) {
            t = lt.nextToken();
            if (t.typeNum() == Token.EOL) {
                return;
            }

            if (needComma) {
                if (! t.isSymbol(',')) {
                    lt.unGetToken();
                    return;
                }
                needComma = false;
                continue;
            }
            if (t.typeNum() == Token.VARIABLE) {
                s.args.add(t);
            } else {
                throw new BasicSyntaxError("malformed INPUT statement.");
            }
            needComma = true;
        }
    }
}
