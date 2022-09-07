package com.github.namazu;
import java.io.File;
import java.io.FileReader;

import com.github.namazu.core.Environment;
import com.github.namazu.core.LexicalUnit;
import com.github.namazu.lexical_parser.LexicalAnalyzer;
import com.github.namazu.lexical_parser.LexicalAnalyzerImpl;
import com.github.namazu.syntax_node.Node;
import com.github.namazu.syntax_node.ProgramNode;

import vavi.util.Debug;

public class Main {

    /**
     * @param args 0: 実行ファイルパス
     */
    public static void main(String[] args) throws Exception {
        String SORCE_PATH = args[0];

        LexicalAnalyzer lex;
        LexicalUnit first;
        Environment env;
        Node program;

        // File Exist Check
        if (!new File(SORCE_PATH).exists()) {
            System.err.println("ソースファイルがないよ？");
                return;
            }

            lex = new LexicalAnalyzerImpl(new FileReader(SORCE_PATH));
            env = new Environment(lex);
            first = lex.get();
            lex.unget(first);
Debug.println("lex: " + lex);

            program = ProgramNode.isMatch(env, first);
Debug.println("program: " + program);
        boolean res = program.parse();
        if (!res) {
            System.err.println("Syntax error");
            return;
        }
        // 構文木表示
        System.err.println("Syntax parsed ---");
        System.err.println(program);
        System.err.println("-----------------");

        System.err.println("-------run!------");
        // プログラム実行
        program.eval();

        System.err.println("\n-----------------");
    }
}
