/*
 * RESTOREStatement.java - Implement the RESTORE Statement.
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

/**
 * The RESTORE statement.
 *
 * The RESTORE statement resets the data cache to the beginning. Normally
 * READ statements read declared data values in sequence until there are
 * no more values to be read, however executing a RESTORE statement will
 * reset the 'data values read' pointer back to the first declared data
 * value.
 *
 * Syntax:
 *      RESTORE
 *
 * Syntax Errors:
 *      Extra stuff past the end of the statement.
 *
 * Runtime Errors:
 *      None.
 */
public class RESTOREStatement extends Statement {

    public RESTOREStatement(LexicalTokenizer lt) throws BasicSyntaxError {
        super(RESTORE);
    }

    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BasicRuntimeError {
        pgm.resetData();
        return pgm.nextStatement(this);
    }

    public String unparse() {
        return "RESTORE";
    }
}
