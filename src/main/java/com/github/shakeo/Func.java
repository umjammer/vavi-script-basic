/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.shakeo;

/**
 * Func.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-07-29 nsano initial version <br>
 */
public abstract class Func extends Command {

    protected Func(String name, int operand_num) {
        super(name, operand_num);
    }

    public void init(Window w, BasicMemory mem) {
        this.w = w;
        this.mem = mem;
    }

    public Variable calc(Variable[] operands) {
        return null;
    }

    protected boolean isSameOperandNum(Variable[] operands) {
        return operand_num == operands.length;
    }
}
