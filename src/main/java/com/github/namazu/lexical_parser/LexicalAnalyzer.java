package com.github.namazu.lexical_parser;

import com.github.namazu.core.LexicalType;
import com.github.namazu.core.LexicalUnit;

public interface LexicalAnalyzer {
    public LexicalUnit get();
    public boolean expect(LexicalType type);
    public void unget(LexicalUnit token);
}
