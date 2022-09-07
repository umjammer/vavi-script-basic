/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.shakeo.function;

import com.github.shakeo.Func;
import com.github.shakeo.Variable;


/**
 * SinFunc.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-07-29 nsano initial version <br>
 */
public class SinFunc extends Func {

    public SinFunc() {
        super("SIN", 1);
    }

    public Variable calc(Variable[] operands) {
        Variable retval = new Variable("retval");
        if (!isSameOperandNum(operands))
            return null;
        if (operands[0].getType() == Variable.TYPE_STRING)
            return null;
        if (operands[0].getType() == Variable.TYPE_INT)
            retval.store(Math.sin(operands[0].getIntegerValue()));
        if (operands[0].getType() == Variable.TYPE_REAL)
            retval.store(Math.sin(operands[0].getRealValue()));
        return retval;
    }
}
