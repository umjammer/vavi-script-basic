/*
 * CommandInterpreter.java -  Provide the basic command line interface.
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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import vavi.util.Debug;


/**
 * This class is an "interactive" BASIC environment. You can think of it as
 * BASIC debug mode. Using the streams you passed in to create the object, it
 * hosts an interactive session allowing the user to enter BASIC programs, run
 * them, save them, and load them.
 */
public class CommandInterpreter {
    private DataInputStream inStream;
    private PrintStream outStream;

    static final String commands[] = {
            "new", "run", "list", "cat", "del", "resume",
            "bye", "save", "load", "dump", "cont",
    };

    static final int CMD_NEW = 0;
    static final int CMD_RUN = 1;
    static final int CMD_LIST = 2;
    static final int CMD_CAT = 3;
    static final int CMD_DEL = 4;
    static final int CMD_RESUME = 5;
    static final int CMD_BYE = 6;
    static final int CMD_SAVE = 7;
    static final int CMD_LOAD = 8;
    static final int CMD_DUMP = 9;
    static final int CMD_CONT = 10;

    /** */
    public CommandInterpreter() {}

    /**
     * Create a new command interpreter attached to the passed
     * in streams.
     */
    public CommandInterpreter(InputStream in, OutputStream out) {
        setInputStream(in);
        setOutputStream(out);
    }

    /** */
    public final void setInputStream(InputStream in) {
        if (in instanceof DataInputStream)
            inStream = (DataInputStream) in;
        else
            inStream = new DataInputStream(in);
    }

    /** */
    public final void setOutputStream(OutputStream out) {
        if (out instanceof PrintStream)
            outStream = (PrintStream) out;
        else
            outStream = new PrintStream(out);
    }

    /**
     * This method basically dispatches the commands of the command
     * interpreter.
     */
    Program processCommand(Program pgm, LexicalTokenizer lt, Token x) {
        Token t;

        switch ((int) x.numValue()) {
        case CMD_RESUME:
            try {
                pgm.resume(inStream, outStream);
            } catch (BasicRuntimeError e) {
                outStream.println(e.getMsg());
            }
            return pgm;
        case CMD_CONT:
            try {
                pgm.cont(inStream, outStream);
            } catch (BasicRuntimeError e) {
                outStream.println(e.getMsg());
            }
            return pgm;

        case CMD_RUN:
            try {
                pgm.run(inStream, outStream);
            } catch (BasicRuntimeError e2) {
                outStream.println(e2.getMsg());
            }
            return pgm;

        case CMD_SAVE:
            t = lt.nextToken();
            if (t.typeNum() != Token.STRING) {
                outStream.println("File name expected for SAVE Command.");
                return pgm;
            }
            outStream.println("Saving file...");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(t.stringValue());
            } catch (IOException except) {
                return pgm;
            }
            PrintStream pp = new PrintStream(fos);
            pgm.list(pp);
            pp.flush();
            try {
                fos.close();
            } catch (IOException except) {
                return pgm;
            }
            return pgm;

        case CMD_LOAD:
            t = lt.nextToken();
            if (t.typeNum() != Token.STRING) {
                outStream.println("File name expected for LOAD command.");
            }
            try {
                pgm = Program.load(t.stringValue(), outStream);
                outStream.println("File loaded.");
            } catch (IOException e) {
                outStream.println("File " + t.stringValue() + " not found.");
                return pgm;
            } catch (BasicSyntaxError bse) {
                outStream.println("Syntax error reading file.");
                outStream.println(bse.getMsg());
                return pgm;
            }
            return pgm;
        case CMD_DUMP:
            PrintStream zzz = outStream;
            t = lt.nextToken();
            if (t.typeNum() == Token.STRING) {
                try {
                    zzz = new PrintStream(Files.newOutputStream(Paths.get(t.stringValue())));
                } catch (IOException ii) {
                }
            }
            pgm.dump(zzz);
            if (zzz != outStream)
                zzz.close();
            return pgm;

        case CMD_LIST:
            t = lt.nextToken();
            if (t.typeNum() == Token.EOL) {
                pgm.list(outStream);
            } else if (t.typeNum() == Token.CONSTANT) {
                int strt = (int) t.numValue();
                t = lt.nextToken();
                if (t.typeNum() == Token.EOL) {
                    pgm.list(strt, outStream);
                } else if (t.isSymbol(',')) {
                    t = lt.nextToken();
                    if (t.typeNum() != Token.CONSTANT) {
                        outStream.println("Illegal parameter to LIST command.");
                        outStream.println(lt.showError());
                        return pgm;
                    }
                    int e = (int) t.numValue();
                    pgm.list(strt, e, outStream);
                } else {
                    outStream.println("Syntax error in LIST command.");
                    outStream.println(lt.showError());
                }
            } else {
                outStream.println("Syntax error in LIST command.");
                outStream.println(lt.showError());
            }
            return pgm;
        }
        outStream.println("Command not implemented.");
        return pgm;
    }

    char[] data = new char[256];

    public Object start() {
        return start(false);
    }

    /**
     * Starts the interactive session. When running the user should see the
     * "Ready." prompt. The session ends when the user types the <code>byte</code>
     * command.
     */
    public Object start(boolean exec) {
        LexicalTokenizer lt = new LexicalTokenizer(data);
        Program pgm = new Program();
        BufferedReader dis = new BufferedReader(new InputStreamReader(inStream));
        String lineData;

        outStream.println("\nJavaBASIC Version 1.0");
        outStream.println("Copyright (C) 1996 Chuck McManis. All Rights Reserved.");

        while (true) {
            Statement s;
            try {
                lineData = dis.readLine();
                Debug.println(Level.FINER, "line: " + lineData);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                outStream.println("Caught an IO exception reading the input stream!");
                return null;
            }

            // exit on eof of the input stream
            if (lineData == null) {
                Debug.println(Level.FINER, "eol");
                if (exec) {
                    try {
                        pgm.run(inStream, outStream);
                    } catch (BasicRuntimeError e2) {
                        outStream.println(e2.getMsg());
                    }
                }
                return pgm;
            }

            // ignore blank lines.
            if (lineData.length() == 0)
                continue;

            lt.reset(lineData);

            if (!lt.hasMoreTokens())
                continue;

            Token t = lt.nextToken();
            switch (t.typeNum()) {
            /*
             * Process one of the command interpreter's commands.
             */
            case Token.COMMAND:
                if (t.numValue() == CMD_BYE)
                    return pgm;
                else if (t.numValue() == CMD_NEW) {
                    pgm = new Program();
//                    System.gc();
                    break;
                } else {
                    pgm = processCommand(pgm, lt, t);
                }
                outStream.println("Ready.\n");
                break;

            /*
             * Process an initial number, it can be a new statement line
             * or it may be an implicit delete command.
             */
            case Token.CONSTANT:
                Token peek = lt.nextToken();
                if (peek.typeNum() == Token.EOL) {
                    pgm.del((int) t.numValue());
                    break;
                } else {
                    lt.unGetToken();
                }
                try {
                    s = ParseStatement.statement(lt);
                    s.addText(lineData);
                    s.addLine((int) t.numValue());
                    pgm.add((int) t.numValue(), s);
                } catch (BasicSyntaxError e) {
                    outStream.println("Syntax Error : " + e.getMsg());
                    outStream.println(lt.showError());
                    continue;
                }
                break;

            /*
             * If initially it is a variable or a statement keyword then it
             * must be an 'immediate' line.
             */
            case Token.VARIABLE:
            case Token.KEYWORD: // immediate mode
            case Token.SYMBOL:
                lt.unGetToken();
                try {
                    s = ParseStatement.statement(lt);
                    do {
                        s = s.execute(pgm, inStream, outStream);
                    } while (s != null);

                } catch (BasicSyntaxError e) {
                    outStream.println("Syntax Error : " + e.getMsg());
                    outStream.println(lt.showError());
                    continue;
                } catch (BasicRuntimeError er) {
                    outStream.println("RUNTIME ERROR.");
                    outStream.println(er.getMsg());
                }
                break;

            /*
             * Blank lines are ignored.
             */
            case Token.EOL:
                break;

            /*
             * Anything else is an error.
             */
            default:
                outStream.println("Error, command not recognized.");
                break;
            }
        }
    }
}
