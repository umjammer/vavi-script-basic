package com.github.namazu.lexical_parser;

import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.Deque;

import com.github.namazu.core.LexicalType;
import com.github.namazu.core.LexicalUnit;

import vavi.util.Debug;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {

    private TokenParser tokenParser;

    public LexicalAnalyzerImpl(FileReader reader) throws Exception {
        tokenParser = new TokenParser(reader);
        this.tokenBuffer = new ArrayDeque<>();
    }

    Deque<LexicalUnit> tokenBuffer;

    @Override
    public LexicalUnit get() {

        if (tokenBuffer.size() == 0) {
            Token nextToken = tokenParser.get();
Debug.println("nextToken: " + nextToken);
            // TokenをLexicalUnitへ
            LexicalUnit nextUnit;
            if (nextToken == null) {
                // nullが返ってきたらEOF
                nextUnit = new LexicalUnit(LexicalType.EOF);
            } else {
                nextUnit = nextToken.parseLexicalUnit();
            }

            tokenBuffer.add(nextUnit);
        }

        return tokenBuffer.poll();
    }

    @Override
    public boolean expect(LexicalType type) {
        return false;
    }

    @Override
    public void unget(LexicalUnit token) {
        this.tokenBuffer.add(token);
    }

    @Override
    public String toString() {
        return tokenBuffer.toString();
    }
}
