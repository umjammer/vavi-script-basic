/*
 * GOSUBStatement.java - Implement the GOSUB Statement.
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

import net.sf.cocoa.basic.BasicSyntaxError;
import net.sf.cocoa.basic.Statement;
import java.io.InputStream;
import java.io.PrintStream;

import net.sf.cocoa.basic.BasicRuntimeError;
import net.sf.cocoa.basic.LexicalTokenizer;
import net.sf.cocoa.basic.Program;
import net.sf.cocoa.basic.Token;

/**
 * The GOSUB statement.
 *
 * Like the GOTO statement, the GOSUB statement unconditionally tranfers
 * control to a non-linear sequence in the program. However, unlike GOTO
 * the position is remembered on the stack so that executing a RETURN
 * statement will return execution to the statement following the GOSUB.
 * The destination is indicated by a line number.
 *
 * Syntax :
 *      GOSUB line
 *
 * Syntax Errors:
 *      Line number required.
 *
 * Runtime Errors:
 *      Non-existent line number.
 */
public class GOSUBStatement extends Statement {

    // This is the line number to transfer control too.
    int lineTarget;

    public GOSUBStatement(LexicalTokenizer lt) throws BasicSyntaxError {
        super(GOSUB);

        parse(this, lt);
    }

    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BasicRuntimeError {
        Statement s;
        pgm.push(this);
        s = pgm.getStatement(lineTarget);
        if (s != null) {
            return s;
        }
        throw new BasicRuntimeError("GOSUB non-existent line "+lineTarget+".");
    }

    public String unparse() {
        return "GOSUB "+lineTarget;
    }

    /**
     * Parse GOSUB Statement.
     */
    private static void parse(GOSUBStatement s, LexicalTokenizer lt) throws BasicSyntaxError {
        Token t = lt.nextToken();
        if (t.typeNum() != Token.CONSTANT) {
            throw new BasicSyntaxError("Line number required after GOSUB.");
        }
        s.lineTarget = (int) t.numValue();
    }
}
