/*
 * READStatement.java - Implement the READ Statement.
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

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.cocoa.basic.BASICRuntimeError;
import net.sf.cocoa.basic.BASICSyntaxError;
import net.sf.cocoa.basic.LexicalTokenizer;
import net.sf.cocoa.basic.Program;
import net.sf.cocoa.basic.Statement;
import net.sf.cocoa.basic.Token;
import net.sf.cocoa.basic.Variable;

/**
 * The READ Statement
 *
 * The READ statement fills variables with preloaded values. These
 * values are preloaded into a FIFO queue using DATA statements.
 * The syntax of the READ statement is :
 *      READ var1, var2, ... varN
 *
 * Syntax Errors:
 *      Malformed READ statement.
 *
 * Runtime Errors:
 *      Out of data.
 *      Type mismatch on read data.
 */
public class READStatement extends Statement {

    List<Token> args;

    public READStatement(LexicalTokenizer lt) throws BASICSyntaxError {
        super(READ);

        parse(this, lt);
    }

    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BASICRuntimeError {
        for (int i = 0; i < args.size(); i++) {
            Variable vi;
            Token q = pgm.popData();
            if (q == null)
                throw new BASICRuntimeError("READ statement is out of data.");
            vi = (Variable) args.get(i);
            if (! vi.isString()) {
                if (q.typeNum() != Token.CONSTANT)
                    throw new BASICRuntimeError("Type mismatch reading variable "+vi);
                pgm.setVariable(vi, q.numValue());
            } else {
                if (q.typeNum() != Token.STRING)
                    throw new BASICRuntimeError("Type mismatch reading variable "+vi);
                pgm.setVariable(vi, q.stringValue());
            }
        }
        return pgm.nextStatement(this);
    }

    public String unparse() {
        StringBuffer sb = new StringBuffer();
        sb.append("READ ");
        for (int i = 0; i < args.size(); i++) {
            Variable vi = (Variable) args.get(i);
            if (i < (args.size()-1)) {
                sb.append(vi.unparse()+", ");
            } else {
                sb.append(vi.unparse());
            }
        }
        return sb.toString();
    }

    /**
     * Parse READ Statement.
     */
    private static void parse(READStatement s, LexicalTokenizer lt) throws BASICSyntaxError {
        Token t;
        s.args = new ArrayList<>();
        boolean needComma = false;

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
                throw new BASICSyntaxError("malformed READ statement.");
            }
            needComma = true;
        }
    }
}
