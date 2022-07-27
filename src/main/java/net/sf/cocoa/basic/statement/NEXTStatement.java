/*
 * NEXTStatement.java - Implement the NEXT Statement.
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

import net.sf.cocoa.basic.BasicRuntimeError;
import net.sf.cocoa.basic.BasicSyntaxError;
import net.sf.cocoa.basic.LexicalTokenizer;
import net.sf.cocoa.basic.Program;
import net.sf.cocoa.basic.Statement;
import net.sf.cocoa.basic.Token;
import net.sf.cocoa.basic.Variable;
import net.sf.cocoa.basic.VariableExpression;
import net.sf.cocoa.util.RedBlackTree;

/**
 * The NEXT statement
 *
 * The NEXT statement causes transfer to return to the line following it
 * corresponding FOR statement.
 *
 * Syntax is :
 *      NEXT [var]
 *
 * Policy:
 *  If the variable name is omitted, then this next matches any FOR
 * statement once. It is still illegal to enter a FOR statement in
 * the middle.
 */
public class NEXTStatement extends Statement {

    // This is the line number to transfer control too.
    Variable myVar;

    public NEXTStatement(LexicalTokenizer lt) throws BasicSyntaxError {
        super(NEXT);

        parse(this, lt);
    }

    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BasicRuntimeError {
        Statement xs;
        FORStatement s;

        /*
         *
         */
        while (true) {
            xs = pgm.pop();
            if (xs == null) {
                throw new BasicRuntimeError("NEXT without FOR");
            }

            if (! (xs instanceof FORStatement)) {
                throw new BasicRuntimeError("Bogus intervening statement: "+xs.asString());
            }
            s = (FORStatement) xs;
            /*
             * Since we have the policy set to be "optional next variable"
             * We use a little trick here to 'bond' the next at run time.
             * When we get here, if the next statement has no variable, we
             * give it the variable of the first FOR statement we pop off
             * the stack.
             */
            if (myVar == null)
                myVar = s.myVar;
            if (s.myVar.name.equalsIgnoreCase(myVar.name))
                break;
            /*
            if (! (s.myVar.name.equalsIgnoreCase(myVar.name))) {
                throw new BasicRuntimeError("NEXT was expecting FOR "+myVar.name+
                            " = ..., not variable '"+s.myVar.name+"'");
            }
            */
        }
        double stepValue = s.sExp.value(pgm);
        if (stepValue == 0)
            throw new BasicRuntimeError("step value of 0.0 in for loop.");
        pgm.setVariable(myVar, pgm.getVariable(myVar)+s.sExp.value(pgm));

        double endValue = s.eExp.value(pgm);
        double currentValue = pgm.getVariable(myVar);
        double startValue = s.nExp.value(pgm);

        if (startValue >= endValue) {
            if ((currentValue < endValue) || (currentValue > startValue)) {
                return pgm.nextStatement(this);
            } else {
                pgm.push(s);
                return pgm.nextStatement(s);
            }
        } else {
            if ((currentValue > endValue) || (currentValue < startValue)) {
                return pgm.nextStatement(this);
            } else {
                pgm.push(s);
                return pgm.nextStatement(s);
            }
        }
    }

    public String unparse() {
        return " NEXT "+((myVar != null) ? myVar.unparse() : "");
    }

    protected RedBlackTree<VariableExpression> getVars() {
        RedBlackTree<VariableExpression> vv = new RedBlackTree<>();
        vv.put(myVar.name, new VariableExpression(myVar));
        return vv;
    }

    /**
     * Parse NEXT Statement.
     */
    private static void parse(NEXTStatement s, LexicalTokenizer lt) throws BasicSyntaxError {
        Token t = lt.nextToken();
        /*
         * Do NEXT statements *require* a variable? In many BASIC implementations
         * the variable is optional.
         */
        if (t.typeNum() != Token.VARIABLE) {
            lt.unGetToken();
            return;
//          throw new BasicSyntaxError("NEXT requires a variable");
        }
        s.myVar = (Variable) t;
        return;
    }
}
