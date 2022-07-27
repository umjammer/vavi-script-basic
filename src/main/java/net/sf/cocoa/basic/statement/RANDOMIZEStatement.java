/*
 * RANDOMIZEStatement.java - Implement the RANDOMIZE Statement.
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

import net.sf.cocoa.basic.BasicRuntimeError;
import net.sf.cocoa.basic.BasicSyntaxError;
import net.sf.cocoa.basic.Statement;
import java.io.InputStream;
import java.io.PrintStream;

import net.sf.cocoa.basic.Expression;
import net.sf.cocoa.basic.LexicalTokenizer;
import net.sf.cocoa.basic.ParseExpression;
import net.sf.cocoa.basic.Program;
import net.sf.cocoa.basic.Token;

/**
 * The RANDOMIZE statement.
 *
 * The RANDOMIZE statement seeds the random number generator. Syntax is:
 *      RANDOMIZE
 */
public class RANDOMIZEStatement extends Statement {
    Expression nExpn;
    boolean useTimeOfDay = false;

    public RANDOMIZEStatement(LexicalTokenizer lt) throws BasicSyntaxError {
        super(RANDOMIZE);
        Token t = lt.nextToken();
        switch (t.typeNum()) {
            case Token.OPERATOR:
            case Token.CONSTANT:
            case Token.VARIABLE:
                lt.unGetToken();
                nExpn = ParseExpression.expression(lt);
            case Token.KEYWORD:
                if (t.numValue() != TIMER)
                    throw new BasicSyntaxError("Badly formed randomize statement.");
                useTimeOfDay = true;
            default:
                lt.unGetToken();
        }
    }

    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BasicRuntimeError {
        if (nExpn != null) {
            pgm.randomize(nExpn.value(pgm));
        } else {
            pgm.randomize((double) System.currentTimeMillis());
        }
        return pgm.nextStatement(this);
    }

    public String unparse() {
        if (nExpn != null)
            return "RANDOMIZE "+nExpn.unparse();
        return "RANDOMIZE";
    }
}
