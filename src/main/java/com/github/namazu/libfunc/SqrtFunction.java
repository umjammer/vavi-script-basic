package com.github.namazu.libfunc;

import com.github.namazu.core.Value;
import com.github.namazu.core.ValueImpl;
import com.github.namazu.syntax_node.ExprListNode;

/**
 * 平方根出す関数
 */
public class SqrtFunction extends Function {
    @Override
    public Value eval(ExprListNode arg) {
        Value val = arg.getValue(0);
        return new ValueImpl(Math.sqrt(val.getDValue()));
    }
}
