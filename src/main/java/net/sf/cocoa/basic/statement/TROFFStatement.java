/*
 * TROFFStatement.java - Implement the TROFF Statement.
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

/**
 * The TROFF statement.
 *
 * The TROFF statement turns TRacing OFF. Syntax is:
 *      TROFF
 */
public class TROFFStatement extends Statement {

    public TROFFStatement(LexicalTokenizer lt) throws BASICSyntaxError {
        super(TROFF);
    }

    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BASICRuntimeError {
        pgm.trace(false);
        return pgm.nextStatement(this);
    }

    public String unparse() {
        return "TROFF";
    }
}
