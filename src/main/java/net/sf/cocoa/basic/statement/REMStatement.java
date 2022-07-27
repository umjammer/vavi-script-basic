/*
 * REMStatement.java - A very simple statement type.
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
 * The REM Statement
 *
 * The REMark statement is used to insert comments into the source code.
 * All text after the REM keyword, up to the end of line, is ignored by
 * the interpreter. The syntax for the statement is:
 *      REM comment text
 */
public class REMStatement extends Statement {
    String comment;

    public REMStatement(LexicalTokenizer lt) throws BasicSyntaxError {
        super(REM);

        comment = lt.asString();
    }

    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BasicRuntimeError {
        return pgm.nextStatement(this);
    }

    public String unparse() {
        return " REM "+comment;
    }
}