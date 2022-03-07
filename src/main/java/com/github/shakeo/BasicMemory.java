
package com.github.shakeo;

import java.util.Stack;


public class BasicMemory {

    /**
     * mainly memory
     */
    public static final int VAR_COUNT = 1024;
    public static final int PROGRAM_LINE_MAX = 65535;
    public Variable[] variables = new Variable[VAR_COUNT];
    public String[] program_memory = new String[PROGRAM_LINE_MAX];

    /**
     * machine status
     */
    public int locate_x = 0, locate_y = 0;
    public int r, g, b;
    public Stack<Integer> callstack = new Stack<>();

    public BasicMemory() {
        for (int i = 0; i < VAR_COUNT; i++) {
            variables[i] = null;
        }
        for (int i = 0; i < PROGRAM_LINE_MAX; i++) {
            program_memory[i] = null;
        }
    }

    public Variable search(String name) {
        for (Variable var : variables) {
            if (var == null)
                continue;
            if (var.getName().equals(name)) {
                return var;
            }
        }
        return null;
    }

    public boolean set(String name, Variable val) {
        Variable setvar;
        if ((setvar = search(name)) == null) {
            return true;
        }
        setvar.store(val);
        return false;
    }

    public boolean newVar(Variable var) {
        boolean set = false;
        for (int i = 0; i < variables.length; i++) {
            if (variables[i] == null) {
                variables[i] = var;
                set = true;
                break;
            }
        }
        return set;
    }
}
