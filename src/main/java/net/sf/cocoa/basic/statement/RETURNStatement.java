/*
 * RETURNStatement.java - Implement the RETURN Statement.
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
import net.sf.cocoa.basic.Statement;
import java.io.InputStream;
import java.io.PrintStream;

import net.sf.cocoa.basic.BasicSyntaxError;
import net.sf.cocoa.basic.LexicalTokenizer;
import net.sf.cocoa.basic.Program;

/**
 * The RETURN statement.
 *
 * The RETURN statement transfers control to the first statement following
 * the last GOSUB to be executed. Note that if the GOSUB used a colon to
 * combine statements on a line, it is that statement that control returns
 * to.
 *
 * Syntax:
 *      RETURN
 *
 * Syntax Errors:
 *      Extra stuff past the end of the statement.
 *
 * Runtime Errors:
 *      Return without GOSUB.
 */
public class RETURNStatement extends Statement {

    public RETURNStatement(LexicalTokenizer lt) throws BasicSyntaxError {
        super(RETURN);
    }

    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BasicRuntimeError {
        Statement s;
        do {
            s = pgm.pop();
            if ((s.keyword == GOSUB) || (s.keyword == ON_GOSUB))
                break;
        } while (s != null);

        if (s == null)
            throw new BasicRuntimeError("RETURN without GOSUB");
        return pgm.nextStatement(s);
    }

    public String unparse() {
        return "RETURN";
    }

}
