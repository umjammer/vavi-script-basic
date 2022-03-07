/*
 * Token.java = one basic token.
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

/**
 * This class is the representation of a single token in the
 * BASIC environment.
 */
public class Token {

    protected Token() { }

    final static String names[] = {
        "symbol", "command", "constant", "function", "keyword",
        "eol",    "string",  "error",    "string variable",
        "numeric variable",  "boolean operator", "operator",
    };

    /** Token is a special symbol (numeric value) */
    public final static int SYMBOL = 0;

    /** Token is a command (number and string) */
    public final static int COMMAND = 1;

    /** Token is a numeric constant (numeric value) */
    public final static int CONSTANT = 2;

    /** Token is a function reference (number and string) */
    public final static int FUNCTION = 3;

    /** Token is a BASIC keyword (number and string) */
    public final static int KEYWORD = 4;

    /** Token indicates the end of an input line */
    public final static int EOL = 5;

    /** Token has a string value */
    public final static int STRING = 6;

    /** Token indicates an error (unknown token) (string value) */
    public final static int ERROR = 7;

    /** Token contains a string variable name (string value) */
    public final static int STRING_VARIABLE = 8;

    /** Token contains a numeric variable name (string value) */
    public final static int NUMERIC_VARIABLE = 9;

    /** Token contains a boolean operator */
    public final static int BOOLEAN_OPERATOR = 10;

    /** Token is an operator in an expression. */
    public final static int OPERATOR = 11;

    /** Token is a variable */
    public final static int VARIABLE = 12;

    protected int     type;     // this token's type
    protected double  nValue;     // Its numeric value (if it has one)
    protected String  sValue;     // Its string value (if it has one)

    public double numValue() {
        return nValue;
    }

    public String stringValue() {
        return sValue;
    }

    public int typeNum() {
        return type;
    }

    String typeString() {
        return names[type];
    }

    /**
     * Create a token with a numeric value.
     */
    Token(int t, double nv) {
        type = t;
        nValue = nv;
    }

    /**
     * Create a token with a string value.
     */
    Token(int t, String sv) {
        type = t;
        sValue = sv;
    }

    /**
     * For operators, create token with numeric value.
     */
    Token(int t, int v) {
        type = t;
        nValue = (double) v;
    }

    /**
     * Create a token with both a string and a numeric value.
     */
     Token(int t, String sv, int iv) {
        type = t;
        sValue = sv;
        nValue = (double) iv;
     }

    public String unparse() {
        switch (type) {
            case STRING:
                return "\""+sValue+"\"";
            case CONSTANT:
                return ""+nValue;
            default:
                return "Token ("+names[type]+")";
        }
    }

    public String toString() {
        return ("TOKEN: Type="+names[type]+", Numeric Value = "+nValue+", String Value = '"+sValue+"'");
    }

    static final boolean isSymbol(Token t, char s) {
        return ((t != null) && (t.typeNum() == SYMBOL) && (t.numValue() == s));
    }

    public final boolean isSymbol(char c) {
        return isSymbol(this, c);
    }

    public final boolean isOp(int op) {
        return ((type == OPERATOR) && ((int) nValue == op));
    }

    public final void negate() {
        if (type != CONSTANT)
            return;
        nValue = -nValue;
    }

 }