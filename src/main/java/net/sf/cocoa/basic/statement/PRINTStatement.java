/*
 * PRINTStatement.java - Implement the PRINT Statement.
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
import java.util.ArrayList;
import java.util.List;

import net.sf.cocoa.basic.BASICRuntimeError;
import net.sf.cocoa.basic.BASICSyntaxError;
import net.sf.cocoa.basic.LexicalTokenizer;
import net.sf.cocoa.basic.ParseExpression;
import net.sf.cocoa.basic.PrintItem;
import net.sf.cocoa.basic.Program;
import net.sf.cocoa.basic.Statement;
import net.sf.cocoa.basic.Token;

/**
 * The PRINT statement.
 *
 * The PRINT statement writes values out to the output stream. It
 * can print both numeric and string exressions.
 *
 * The syntax of the PRINT statement is :
 *      PRINT   Expression [, Expression] | [; Expression]
 *
 * Items separated by a semicolon will have no space between them, items
 * separated by a comma will have a tab inserted between them.
 *
 * Syntax Errors:
 *      Unexpected symbol in input.
 */
public class PRINTStatement extends Statement {

    // This is the line number to transfer control too.
    List<PrintItem> args;

    public PRINTStatement(LexicalTokenizer lt) throws BASICSyntaxError {
        super(PRINT);
        parse(this, lt);
    }

    protected Statement doit(Program pgm, InputStream in, PrintStream out) throws BASICRuntimeError {
        PrintItem pi = null;
        int col = 0;
        for (int i = 0; i < args.size(); i++) {
            String z;
            pi = (PrintItem)(args.get(i));
            z = pi.value(pgm, col);
            out.print(z);
            col += z.length();
        }
        if ((pi == null) || pi.needCR()) {
            out.print("\n");
        }
        return pgm.nextStatement(this);
    }

    public String unparse() {
        StringBuffer sb = new StringBuffer();
        sb.append("PRINT ");
        for (int i = 0; i < args.size(); i++) {
            PrintItem pi = (PrintItem)(args.get(i));
            sb.append(pi.unparse());
        }
        return sb.toString();
    }

    private static List<PrintItem> parseStringExpression(LexicalTokenizer lt) throws BASICSyntaxError {
        List<PrintItem> result = new ArrayList<>();
        Token t;

        while (true) {
            t = lt.nextToken();
            switch (t.typeNum()) {
                case Token.CONSTANT:
                case Token.FUNCTION:
                case Token.VARIABLE:
                case Token.STRING:
                case Token.OPERATOR:
                    lt.unGetToken();
                    result.add(new
                            PrintItem(PrintItem.EXPRESSION, ParseExpression.expression(lt)));
                    break;
                case Token.SYMBOL:
                    switch ((int) t.numValue()) {
                        case '(' :
                            lt.unGetToken();
                            result.add(new PrintItem(PrintItem.EXPRESSION, ParseExpression.expression(lt)));
                            break;
                        case ';':
                            result.add(new PrintItem(PrintItem.SEMI, null));
                            break;
                        case ',':
                            result.add(new PrintItem(PrintItem.TAB, null));
                            break;
                        default:
                            lt.unGetToken();
                            return result;
                    }
                    break;
                case Token.EOL:
                    return result;
                default:
                    lt.unGetToken();
                    return result;
            }
        }
    }

    private static void parse(PRINTStatement s, LexicalTokenizer lt) throws BASICSyntaxError {
        s.args = parseStringExpression(lt);
    }
}
