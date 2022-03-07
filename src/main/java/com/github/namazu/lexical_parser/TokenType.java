package com.github.namazu.lexical_parser;

import java.util.regex.Pattern;

/**
 * 一番最初に切り出すTokenのタイプ
 * @author namaz
 */
public enum TokenType {
    /** 0-9から始まりdotを含んで連続する(笑) */
    NUMBER("[0-9.]+"),
    /** A-Za-zから始まって単語構成文字が連続する */
    WORD("^[a-zA-Z]\\w*"),
    /** "から始まりε "があったらそれでおしまい.（エスケープシーケンス） */
    LITERAL("^\"[^\"]*\"?"),
    /** １文字で構成される字句 */
    SINGLE_OPERATOR("[\\.\n\\+\\-\\*\\/\\)\\(,]"),
    /** 複数文字で構成されうる演算子 > < = => >= =< <= <> */
    MULTY_OPERATOR("[><=]|=[><]|[><]=|<>");

    private final Pattern pattern;

    private TokenType(String regx) {
        this.pattern = Pattern.compile(regx);
    }

    public boolean isMatch(String test) {
        return pattern.matcher(test).matches();
    }
}
