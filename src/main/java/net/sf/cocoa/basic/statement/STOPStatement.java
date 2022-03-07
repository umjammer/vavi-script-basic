/*
 * STOPStatement.java - Implement the STOP Statement.
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
 * The STOP Statement
 *
 * The STOP statement halts execution of the program, but remembers where
 * the program stopped. It can be restarted by calling the resume() method
 * of Program or using the RESUME command in the command interpreter. When
 * the STOP statement is encountered the program prints "STOP at line ###"
 * on the standard output. The syntax of this command is :
 *      STOP
 *
 * Runtime errors:
 *      Program wasn't STOPped so cannot be resumed.
 */
public class STOPStatement extends Statement {

    public STOPStatement(LexicalTokenizer lt) throws BASICSyntaxError {
        super(STOP);
    }

    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BASICRuntimeError {
        out.println("STOP at line : "+line);
        pgm.push(this);
        return null;
    }

    public String unparse() {
        return "STOP";
    }

}
