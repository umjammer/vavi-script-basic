package com.github.namazu.syntax_node;

import java.util.logging.Level;

import com.github.namazu.core.Environment;
import com.github.namazu.core.LexicalType;
import com.github.namazu.core.LexicalUnit;
import com.github.namazu.core.Value;

import vavi.util.Debug;

/**
 * 代入文クラス
 */
public class AssignStmtNode extends Node {

    LexicalUnit name;
    Node exprNode;

    public AssignStmtNode(NodeType type, Environment env) {
        super(type, env);
    }

    public static Node isMatch(Environment env, LexicalUnit first) {
        // 代入文とCallFancはLL(2）なので
        if (first.getType() != LexicalType.NAME) {
Debug.println("no name");
            return null;
        }
        env.getInput().get();
        LexicalUnit second = env.getInput().get();

        env.getInput().unget(first);
        env.getInput().unget(second);
        if (second.getType() != LexicalType.EQ) {
Debug.println("no eq");
            return null;
        }

        return new AssignStmtNode(NodeType.ASSIGN_STMT, env);
    }

    @Override
    public boolean parse() {
        name = env.getInput().get();
        // skip eq
        if (!skipExpectNode(LexicalType.EQ)) {
            return false;
        }

        // exprNodeが期待される.
        exprNode = ExprNode.isMatch(env, peekLexicalUnit());
        if (exprNode != null) {
            return exprNode.parse();
        }
        return false;
    }

    @Override
    public String toString() {
        // a = 5 -> a[5]
        return String.format("%s[%s]", name.getValue().getSValue(), exprNode.toString());
    }

    @Override
    public Value eval() {
        // 変数テーブルに入れる
        Value value = exprNode.eval();
Debug.println(Level.FINE, name + "に" + value.getSValue() + "をいれる！");
        env.setVarValue(name, value);
        return null;
    }
}
