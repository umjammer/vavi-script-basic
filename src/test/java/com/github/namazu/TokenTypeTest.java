/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.namazu;

import org.junit.jupiter.api.Test;

import com.github.namazu.lexical_parser.TokenType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenTypeTest {

    @Test
    void test() throws Exception {

        TokenType testTokenType;
        // NUMBER
        testTokenType = TokenType.NUMBER;
        tokenPatternTest("10", testTokenType, true);
        tokenPatternTest("hoge", testTokenType, false);
        tokenPatternTest("1hoge", testTokenType, false);

        // WORD
        testTokenType = TokenType.WORD;
        tokenPatternTest("Aa", testTokenType, true);
        tokenPatternTest("aab", testTokenType, true);
        tokenPatternTest("a1", testTokenType, true);
        tokenPatternTest("1", testTokenType, false);
        tokenPatternTest("b", testTokenType, true);

        // LITERAL
        testTokenType = TokenType.LITERAL;
        tokenPatternTest("\"", testTokenType, true);
        tokenPatternTest("\"hoge", testTokenType, true);
        tokenPatternTest("\"hoge\"", testTokenType, true);
        tokenPatternTest("hoge", testTokenType, false);
        tokenPatternTest("\"hoge\"hoge", testTokenType, false);

        // SIGLE_OPERATOR
        testTokenType = TokenType.SINGLE_OPERATOR;
        tokenPatternTest(".", testTokenType, true);
        tokenPatternTest("\n", testTokenType, true);
        tokenPatternTest("+", testTokenType, true);
        tokenPatternTest("-", testTokenType, true);
        tokenPatternTest("*", testTokenType, true);
        tokenPatternTest("/", testTokenType, true);
        tokenPatternTest(")", testTokenType, true);
        tokenPatternTest("(", testTokenType, true);

        // MULTY_OPERATOR
        testTokenType = TokenType.MULTY_OPERATOR;
        tokenPatternTest(">", testTokenType, true);
        tokenPatternTest("<", testTokenType, true);
        tokenPatternTest(">=", testTokenType, true);
        tokenPatternTest("<=", testTokenType, true);
        tokenPatternTest("=", testTokenType, true);
        tokenPatternTest("=>", testTokenType, true);
        tokenPatternTest("=<", testTokenType, true);
        tokenPatternTest("<>", testTokenType, true);
        tokenPatternTest("><", testTokenType, false);

    }

    private static void tokenPatternTest(String test, TokenType type, boolean expect) throws Exception {
        assertEquals(type.isMatch(test), expect, type.name());
    }
}

/* */
