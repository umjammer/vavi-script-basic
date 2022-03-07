package com.github.namazu.libfunc;

import com.github.namazu.core.Value;
import com.github.namazu.syntax_node.ExprListNode;

public class PrintFunction extends Function {

    @Override
    public Value eval(ExprListNode arg) {
        Value value = arg.getValue(0);
        System.out.print(value.getSValue());
        return null;
    }
}
