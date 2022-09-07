
package com.github.shakeo;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;

import vavi.util.Debug;


public class BasicParser {

    public interface View {
        void print(String s);
    }

    BasicLexer lexer;
    BasicMemory mem;
    View w;
    int token;
    int linenum;

    List<Command> allCommands = new ArrayList<>();

    List<Func> allFuncs = new ArrayList<>();

    BasicParser(View wp) {
        w = wp;
        for (Command c : ServiceLoader.load(Command.class)) {
            c.init(w, mem);
            allCommands.add(c);
        }
        for (Func f : ServiceLoader.load(Func.class)) {
            f.init(w, mem);
            allFuncs.add(f);
        }
    }

    public void exec(String line_str, BasicMemory mem, int linenum) {
        lexer = new BasicLexer(line_str);
        this.mem = mem;
        this.linenum = linenum;
        statement();
        w.print("\n");
    }

    private Variable statement() {
        if (getToken(null))
            return new Variable("success"); // empty statement
        switch (token) {
        case BasicLexer.NAME:
            String name_val = lexer.getStringValue();
            getToken(null);
            if (token == BasicLexer.OPERATOR && lexer.getStringValue().equals("=")) {
                if (getToken("SYNTAX ERROR"))
                    return null;
                if (assignmentStatement(name_val) == null) {
                    return null;
                }
                break;
            }
            if (commandStatement(name_val) == null) {
                return null;
            }
            break;
        }
        return new Variable("success");
    }

    private Variable assignmentStatement(String varname) {
        Variable eval_expr, assign_var;
        eval_expr = expr();
        if ((assign_var = mem.search(varname)) == null) {
            if (!mem.newVar(new Variable(varname))) {
                error("NOT ENOUGH MEMORY");
                return null;
            }
            assign_var = mem.search(varname);
        }
        assign_var.store(eval_expr);
        return new Variable("success");
    }

    private Variable commandStatement(String commandname) {
        String returncode = null;
        for (Command c : allCommands) {
            if (c.name.equals(commandname)) {
                Variable[] operands;
                if (c.operand_num != -1) {
Debug.println("openum " + c.operand_num);
                    operands = new Variable[c.operand_num];
                    for (int i = 0;; i++) {
                        if (i >= c.operand_num) {
                            error("TOO MUCH ARGUMENT");
                            return null;
                        }
                        if (getToken("TOO FEW ARGUMENTS"))
                            return null;
                        Variable o = expr();
                        if (o == null) {
                            return null;
                        }
                        operands[i] = o;
                        if (token != BasicLexer.COMMA)
                            break;
                    }
                } else {
                    ArrayList<Variable> operand_list = new ArrayList<>();
                    while (true) {
                        Variable o = expr();
                        if (o == null) {
                            return null;
                        }
                        operand_list.add(o);
                        if (token != BasicLexer.COMMA)
                            break;
                    }
                    operands = operand_list.toArray(new Variable[0]);
                }
                returncode = c.exec(operands);
                break;
            }
        }
        if (returncode == null) {
            error("COMMAND " + commandname + " NOT FOUND");
            return null;
        } else if (returncode.equals("success")) {
            return new Variable("success");
        } else {
            error(returncode);
            return null;
        }
    }

    private Variable expr() {
        Debug.println(Level.FINE, "expr");
        Variable eval_val, second_eval_val;
        eval_val = expr2();
        if (eval_val == null) {
            return null;
        }
        if (token == BasicLexer.NAME) {
            if (lexer.getStringValue().equals("AND") || lexer.getStringValue().equals("OR") ||
                lexer.getStringValue().equals("XOR")) {
                if (getToken("EXPRESSION NOT FOUND AFTER \"" + lexer.getStringValue() + "\""))
                    return null;
                if ((second_eval_val = expr()) == null)
                    return null;
                if ((eval_val = eval_val.bitOperation(second_eval_val, lexer.getStringValue())) == null) {
                    error("TYPE MISMATCH");
                    return null;
                }
            }
        }
        return eval_val;
    }

    private Variable expr2() {
        Variable eval_val, second_eval_val;
        eval_val = form();
        if (eval_val == null) {
            return null;
        }
        if (token == BasicLexer.OPERATOR) {
            if (lexer.getStringValue().equals("==") || lexer.getStringValue().equals("!=") ||
                lexer.getStringValue().equals(">") || lexer.getStringValue().equals("<") ||
                lexer.getStringValue().equals(">=") || lexer.getStringValue().equals("<=")) {
                if (getToken("EXPRESSION NOT FOUND AFTER \"" + lexer.getStringValue() + "\""))
                    return null;
                if ((second_eval_val = expr2()) == null)
                    return null;
                if ((eval_val = eval_val.relation(second_eval_val, lexer.getStringValue())) == null) {
                    error("TYPE MISMATCH");
                    return null;
                }
            }
        }
        return eval_val;
    }

    private Variable form() {
        Variable eval_val;
        eval_val = term();
        if (eval_val == null) {
            return null;
        }
        if (token == BasicLexer.OPERATOR) {
            Variable second_eval_val;
            if (lexer.getStringValue().equals("+")) {
                if (getToken("EXPRESSION NOT FOUND AFTER \"+\""))
                    return null;
                if ((second_eval_val = form()) == null)
                    return null;
                if ((eval_val = eval_val.add(second_eval_val)) == null) {
                    error("TYPE MISMATCH");
                    return null;
                }
            } else if (lexer.getStringValue().equals("-")) {
                if (getToken("EXPRESSION NOT FOUND AFTER \"-\""))
                    return null;
                if ((second_eval_val = form()) == null)
                    return null;
                if ((eval_val = eval_val.sub(second_eval_val)) == null) {
                    error("TYPE MISMATCH");
                    return null;
                }
            }
        }
        return eval_val;
    }

    private Variable term() {
        Variable eval_val;
        if (token == BasicLexer.OPERATOR && lexer.getStringValue().equals("-")) {
            if (getToken("EXPRESSION NOT FOUND AFTER \"-\""))
                return null;
            eval_val = term();
            if (eval_val == null) {
                return null;
            }
            eval_val = eval_val.pm();
            if (eval_val == null) {
                error("TYPE MISMATCH");
                return null;
            }
        } else if (token == BasicLexer.OPERATOR && lexer.getStringValue().equals("(")) {
            if (getToken("EXPRESSION NOT FOUND AFTER \"(\""))
                return null;
            eval_val = expr();
            if (!(token == BasicLexer.OPERATOR && lexer.getStringValue().equals(")"))) {
                error("')' NOT FOUND");
                return null;
            }
            getToken(null);
        } else {
            eval_val = mono();
            if (eval_val == null) {
                return null;
            }
            if (token == BasicLexer.OPERATOR) {
                Variable second_eval_val;
                if (lexer.getStringValue().equals("*")) {
                    if (getToken("EXPRESSION NOT FOUND AFTER \"*\""))
                        return null;
                    if ((second_eval_val = term()) == null)
                        return null;
                    if ((eval_val = eval_val.mul(second_eval_val)) == null) {
                        error("TYPE MISMATCH");
                        return null;
                    }
                } else if (lexer.getStringValue().equals("/")) {
                    if (getToken("EXPRESSION NOT FOUND AFTER \"/\""))
                        return null;
                    if ((second_eval_val = term()) == null)
                        return null;
                    if ((eval_val = eval_val.div(second_eval_val)) == null) {
                        error("TYPE MISMATCH");
                        return null;
                    }
                    if (eval_val.getName().equals("_div0")) {
                        error("DIVISION BY 0");
                        return null;
                    }
                }
            }
        }
        return eval_val;
    }

    private Variable mono() {
        Variable eval_val = new Variable("temp");
        if (token == BasicLexer.NUMBER) {
            if (lexer.numberIsReal()) {
                eval_val.store(lexer.getRealValue());
            } else {
                eval_val.store(lexer.getIntegerValue());
            }
            getToken(null);
        } else if (token == BasicLexer.STRING) {
            eval_val.store(lexer.getStringValue());
            getToken(null);
        } else if (token == BasicLexer.NAME) {
            String name = lexer.getStringValue();
            getToken(null);
            if (token == BasicLexer.OPERATOR && lexer.getStringValue().equals("(")) {
                for (Func f : allFuncs) {
                    if (f == null)
                        continue;
                    if (f.name.equals(name)) {
                        Variable[] operands = new Variable[f.operand_num];
                        for (int i = 0; i < f.operand_num; i++)
                            operands[i] = null;
                        for (int i = 0;; i++) {
                            if (i >= f.operand_num) {
                                error("TOO MUCH ARGUMENT");
                                return null;
                            }
                            if (getToken("TOO FEW ARGUMENTS"))
                                return null;
                            Variable o = expr();
                            if (o == null) {
                                return null;
                            }
Debug.println(o.getName());
                            operands[i] = o;
                            if (token != BasicLexer.COMMA)
                                break;
                        }
                        for (Variable v : operands) {
Debug.println("v=" + v);
                            if (v == null) {
Debug.println("#");
                                error("TOO FEW ARGUMENTS");
                                return null;
                            }
Debug.println("!");
                        }
                        if (!(token == BasicLexer.OPERATOR && lexer.getStringValue().equals(")"))) {
                            error("')' NOT FOUND");
                            return null;
                        }
                        eval_val = f.calc(operands);
                        if (eval_val == null) {
                            error("IN " + name + ", SOME ERROR HAS OCCURED");
                            return null;
                        }
                        getToken(null);
                        break;
                    }
                }
            } else {
                eval_val = mem.search(name);
                if (eval_val == null) {
                    error("VARIABLE " + name + " IS REFERENCED BEFORE ASSIGNMENT");
                    return null;
                }
            }
        } else {
            return null;
        }
        return eval_val;
    }

    private boolean getToken(String errormsg) {
        token = lexer.getToken();
        if (token == BasicLexer.END) {
            if (errormsg != null)
                error(errormsg);
            return true;
        }
        return false;
    }

    private void error(String msg) {
        w.print(msg + (linenum != -1 ? " IN LINE " + String.valueOf(linenum) : "") + "\n");
    }
}
