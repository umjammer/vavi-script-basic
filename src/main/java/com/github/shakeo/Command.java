/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.shakeo;

/**
 * Command.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-07-29 nsano initial version <br>
 */
public abstract class Command {

    public String name;
    public int operand_num;
    protected BasicParser.View w;
    protected BasicMemory mem;

    protected Command(String name, int operand_num) {
        this.name = name;
        this.operand_num = operand_num;
    }

    public void init(BasicParser.View w, BasicMemory mem) {
        this.w = w;
        this.mem = mem;
    }

    public String exec(Variable[] operands) {
        // virtual method
        return "success";
    }
}
