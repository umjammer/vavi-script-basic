/*
 * GOTOStatement.java - Implement the GOTO Statement.
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

import net.sf.cocoa.basic.Statement;
import java.io.InputStream;
import java.io.PrintStream;

import net.sf.cocoa.basic.BASICRuntimeError;
import net.sf.cocoa.basic.BASICSyntaxError;
import net.sf.cocoa.basic.LexicalTokenizer;
import net.sf.cocoa.basic.Program;
import net.sf.cocoa.basic.Token;

/**
 * The GOTO statement.
 *
 * The GOTO statement unconditionally tranfers control to a non-linear
 * sequence in the program. The destination is indicated by a line number.
 *
 * Syntax :
 *      GOTO line
 *
 * Syntax Errors:
 *      Line number required.
 *
 * Runtime Errors:
 *      Non-existent line number.
 */
public class GOTOStatement extends Statement {

    // This is the line number to transfer control too.
    int lineTarget;

    public GOTOStatement(LexicalTokenizer lt) throws BASICSyntaxError {
        super(GOTO);

        parse(this, lt);
    }

    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BASICRuntimeError {
        Statement s;
        s = pgm.getStatement(lineTarget);
        if (s != null) {
            return s;
        }
        throw new BASICRuntimeError("GOTO non-existent line "+lineTarget+".");
    }

    public String unparse() {
        return "GOTO "+lineTarget;
    }

    /**
     * Parse GOTO Statement.
     */
    private static void parse(GOTOStatement s, LexicalTokenizer lt) throws BASICSyntaxError {
        Token t = lt.nextToken();
        if (t.typeNum() != Token.CONSTANT) {
            throw new BASICSyntaxError("Line number required after GOTO.");
        }
        s.lineTarget = (int) t.numValue();
    }

}