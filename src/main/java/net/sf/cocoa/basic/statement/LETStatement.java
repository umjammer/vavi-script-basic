/*
 * LETStatement.java - Implement the LET Statement.
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
import net.sf.cocoa.basic.BooleanExpression;
import net.sf.cocoa.basic.Expression;
import net.sf.cocoa.basic.LexicalTokenizer;
import net.sf.cocoa.basic.ParseExpression;
import net.sf.cocoa.basic.Program;
import net.sf.cocoa.basic.Statement;
import net.sf.cocoa.basic.Token;
import net.sf.cocoa.basic.Variable;
import net.sf.cocoa.basic.VariableExpression;
import net.sf.cocoa.util.RedBlackTree;

/**
 * The LET statement.
 *
 * The LET statement is the standard assignment statement in BASIC.
 * Technically its syntax is:
 *      LET var = expression
 * However we allow you to omit the LET or simply use
 *      var = expression.
 *
 * Syntax errors :
 *      missing = in assignment statement.
 *      string assignment needs string expression.
 *      Boolean expression not allowed in LET.
 *      unmatched parenthesis in LET statement.
 */
public class LETStatement extends Statement {

    Variable myVar;
    Expression nExp;

    public LETStatement(LexicalTokenizer lt) throws BasicSyntaxError {
        super(LET);

        parse(this, lt);
    }

    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BasicRuntimeError {
        if (myVar.isString()) {
            pgm.setVariable(myVar, nExp.stringValue(pgm));
        } else {
            pgm.setVariable(myVar, nExp.value(pgm));
        }
        return pgm.nextStatement(this);
    }

    public String unparse() {
        String sb = "LET " +
                myVar.unparse() +
                " = " +
                nExp.unparse();
        return sb;
    }

    /**
     * Generate a trace record for the LET statement.
     */
    protected RedBlackTree<VariableExpression> getVars() {
        RedBlackTree<VariableExpression> vv = new RedBlackTree<>();
        nExp.trace(vv);
        return (vv);
    }


    /**
     * Parse LET Statement.
     */
    private static void parse(LETStatement s, LexicalTokenizer lt) throws BasicSyntaxError {
        Token t = lt.nextToken();

        if (t.typeNum() != Token.VARIABLE)
            throw new BasicSyntaxError("variable expected for LET statement.");

        s.myVar = (Variable) t;
        t = lt.nextToken();
        if (! t.isOp(Expression.OP_EQ))
            throw new BasicSyntaxError("missing = in assignment statement.");
        s.nExp = ParseExpression.expression(lt);
        if (s.myVar.isString() && ! s.nExp.isString()) {
            throw new BasicSyntaxError("String assignment needs string expression.");
        }
        if (s.nExp instanceof BooleanExpression) {
            throw new BasicSyntaxError("Boolean expression not allowed in LET.");
        }
        t = lt.nextToken();
        if (t.isSymbol(')'))
            throw new BasicSyntaxError("unmatched parenthesis in LET statement.");
        else
            lt.unGetToken();
        return;
    }
}
