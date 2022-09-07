/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.shakeo.command;

import com.github.shakeo.Command;
import com.github.shakeo.Variable;


/**
 * PrintCommand.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-07-29 nsano initial version <br>
 */
public class PrintCommand extends Command {

    public PrintCommand() {
        super("PRINT", -1);
    }

    @Override
    public String exec(Variable[] operands) {
        for (Variable v : operands) {
            w.print(v.toString());
        }
        return "success";
    }
}
