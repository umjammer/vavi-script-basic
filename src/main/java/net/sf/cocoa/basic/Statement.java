/*
 * Statement.java - BASIC Statement object
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
package net.sf.cocoa.basic;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;

import net.sf.cocoa.util.RedBlackTree;

/**
 * This class defines BASIC statements. As with expressions, there is a
 * subclass of Statement that does parsing called ParseStatement. This
 * separation allows the statement object to be half as large as it might
 * otherwise be.
 *
 * The <i>execute</i> interface defines what the BASIC statements *do*.
 * These are all called by the containing <b>Program</b>.
 */
public abstract class Statement {
    public int keyword; // type of statement
    protected int line;

    private String orig;    // original string that was parsed into this statement.

    /**
     * These are all of the statement keywords we can parse.
     */
    protected final static String[] keywords = {
        "*NONE*", "goto", "gosub", "return", "print",
         "if", "then", "end", "data", "restore", "read",
         "on", "rem", "for", "to", "next", "step", "gosub",
         "goto", "let", "input", "stop", "dim", "randomize",
         "tron", "troff", "timer",
    };

    /**
     * This constants should match the indexes of the above keywords.
     */
    protected final static int NONE = 0; // invalid statement
    protected final static int GOTO = 1;
    protected final static int GOSUB = 2;
    protected final static int RETURN = 3;
    protected final static int PRINT = 4;
    protected final static int IF = 5;
    protected final static int THEN = 6;
    protected final static int END = 7;
    protected final static int DATA = 8;
    protected final static int RESTORE = 9;
    protected final static int READ = 10;
    protected final static int ON = 11;
    protected final static int REM = 12;
    protected final static int FOR = 13;
    protected final static int TO = 14;
    protected final static int NEXT = 15;
    protected final static int STEP = 16;
    protected final static int ON_GOSUB = 17;
    protected final static int ON_GOTO = 18;
    protected final static int LET = 19;
    protected final static int INPUT = 20;
    protected final static int STOP = 21;
    protected final static int DIM = 22;
    protected final static int RANDOMIZE = 23;
    protected final static int TRON = 24;
    protected final static int TROFF = 25;
    protected final static int TIMER = 26; // not a real statement

    public Statement nxt;  // if there are chained statements

    /**
     * Construct a new statement object with a valid key.
     */
    protected Statement(int key) {
        keyword = key;
    }

    /**
     * This method does the actual statement execution. It works by calling the
     * abstract function 'doit' which is defined in each statement subclass. The
     * runtime error (if any) is caught so that the line number and statement can
     * be attached to the result and then it is re-thrown.
     */
    Statement execute(Program pgm, InputStream in, PrintStream out) throws BasicRuntimeError {
        Statement nxt = null;
        try {
            nxt = doit(pgm, in, out);
        } catch (BasicRuntimeError e) {
            throw new BasicRuntimeError(this, e.getMsg());
        }
        return nxt;
    }

    /**
     * Return a string representation of this statement. If the
     * original text was set then use that, otherwise reconstruct
     * the string from the parse tree.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BASIC Statement :");
        if (orig != null) {
            sb.append(orig);
        } else {
            sb.append(unparse());
        }
        return sb.toString();
    }

    /**
     * Put a reference to the original string from which this statement was parsed.
     * this can be used for listing out the program in the native case style of the user.
     */
    void addText(String t) { orig = t; }

    /**
     * Return the statement as a string.
     */
    public String asString() { return orig; }

    /**
     * Update line number information in this statement. Used to determine the next
     * line to execute.
     */
    public void addLine(int l) {
        line = l;
        if (nxt != null)
            nxt.addLine(l);
    }

    /**
     * Return this statements line number.
     */
    int lineNo() { return line; }

    /**
     * reconstruct the statement from the parse tree, this is most useful for
     * diagnosing parsing problems.
     */
    public abstract String unparse();

    /**
     * This method "runs" this statement and returns a reference on
     * the next statement to run or null if there is no next statement.
     *
     * @throws BasicRuntimeError if there is a problem during statement
     * execution such as divide by zero etc.
     */
    protected abstract Statement doit(Program pgm, InputStream in, PrintStream out)
    throws BasicRuntimeError;

    protected RedBlackTree<VariableExpression> vars; // variables used by this statement.
    /**
     * The trace method can be used during execution to print out what
     * the program is doing.
     */
    void trace(Program pgm, PrintStream ps) {
        StringBuilder sb = new StringBuilder();
        String n;
        sb.append("**:");

        if (vars == null)
            vars = getVars();

        /*
         * Print the line we're executing on the output stream.
         */
        n = line+"";
        for (int zz = 0; zz < 5 - n.length(); zz++)
            sb.append(' ');
        sb.append(n);
        sb.append(':');
        sb.append(unparse());
        ps.println(sb);
        if (vars != null) {
            for (Enumeration<VariableExpression> e = vars.elements(); e.hasMoreElements(); ) {
                VariableExpression vi = e.nextElement();
                String  t;
                try {
                    t = vi.stringValue(pgm);
                } catch (BasicRuntimeError bse) {
                    t = "Not yet defined.";
                }
                ps.println("        :"+vi.unparse()+" = "+(vi.isString() ? "\""+t+"\"" : t));
            }
        }
    }

    /**
     * Can be overridden by statements that use variables in their execution.
     */
    protected RedBlackTree<VariableExpression> getVars() {
        return null;
    }
}
