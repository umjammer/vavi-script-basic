package com.github.namazu.syntax_node;

public enum NodeType {
    PROGRAM,
    STMT_LIST,
    STMT,
    FOR_STMT,
    ASSIGN_STMT,
    BLOCK,
    IF_BLOCK,
    LOOP_BLOCK,
    COND,
    EXPR_LIST,
    EXPR,
    FUNCTION_CALL,
    STRING_CONSTANT,
    INT_CONSTANT,
    DOUBLE_CONSTANT,
    END,
}