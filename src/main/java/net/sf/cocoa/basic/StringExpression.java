/*
 * StringExpression.java - parse and execute the string "expressions".
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

class StringExpression extends Expression {

    StringExpression(int op, Expression a, Expression b) throws BasicSyntaxError {
        super(op, a, b);
    }

    public double value(Program pgm) throws BasicRuntimeError {
        switch (oper) {
            case OP_EQ:
                return (arg1.stringValue(pgm).compareTo(arg2.stringValue(pgm)) == 0) ? 1 : 0;
            case OP_NE:
                return (arg1.stringValue(pgm).compareTo(arg2.stringValue(pgm)) != 0) ? 1 : 0;
            case OP_LT:
                return (arg1.stringValue(pgm).compareTo(arg2.stringValue(pgm)) < 0) ? 1 : 0;
            case OP_LE:
                return (arg1.stringValue(pgm).compareTo(arg2.stringValue(pgm)) <= 0) ? 1 : 0;
            case OP_GT:
                return (arg1.stringValue(pgm).compareTo(arg2.stringValue(pgm)) > 0) ? 1 : 0;
            case OP_GE:
                return (arg1.stringValue(pgm).compareTo(arg2.stringValue(pgm)) >= 0) ? 1 : 0;
            default:
                return super.value(pgm);
        }
    }

    String stringValue(Program pgm, int c)  throws BasicRuntimeError {
        switch (oper) {
            case OP_ADD:
                String z = arg1.stringValue(pgm, c);
                int c2 = c + z.length();
                return z + arg2.stringValue(pgm, c2);
            default:
                throw new BasicRuntimeError("Unknown operator in string expression.");
        }
    }

    public String stringValue(Program pgm) throws BasicRuntimeError {
        return stringValue(pgm, 0);
    }

    public boolean isString() {
        return true;
    }
}