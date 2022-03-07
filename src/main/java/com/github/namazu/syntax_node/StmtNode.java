package com.github.namazu.syntax_node;

import com.github.namazu.core.Environment;
import com.github.namazu.core.LexicalType;
import com.github.namazu.core.LexicalUnit;
import com.github.namazu.core.Value;

/**
 *
 * <stmt>  ::=
 *        <subst>
 *        | <call_sub>
 *        | <FOR> <subst> <TO> <INTVAL> <NL> <stmt_list> <NEXT> <NAME>
 *        | <END>
 */
public class StmtNode extends Node {
    private Node childNode;

    public StmtNode(NodeType type, Environment env) {
        super(type, env);
    }

    static FirstCollection fc = new FirstCollection(
        LexicalType.FOR,
        LexicalType.END,
        LexicalType.NAME // STMT
    );

    public static Node isMatch(Environment env, LexicalUnit first) {
        if (fc.contains(first)) {
            Node node = new StmtNode(NodeType.STMT, env);
            return node;
        }
        return null;
    }

    @Override
    public boolean parse() {
        @SuppressWarnings("unchecked")
        NextNodeList nextNodeList = new NextNodeList(
            AssignStmtNode.class,
            FunctionCallNode.class,
            ForStmtNode.class,
            EndNode.class
        );
        childNode = nextNodeList.nextNode(env, peekLexicalUnit());
        if (childNode != null) {
            return childNode.parse();
        }
        return false;
    }

    @Override
    public Value eval() {
        return childNode.eval();
    }

    @Override
    public String toString() {
        if (childNode == null) {
            return "";
        }
        return childNode.toString();
    }

}
