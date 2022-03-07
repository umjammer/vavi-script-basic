package com.github.namazu.libfunc;

import com.github.namazu.core.Value;
import com.github.namazu.syntax_node.ExprListNode;

public abstract class Function {
    public abstract Value eval(ExprListNode arg);
}
