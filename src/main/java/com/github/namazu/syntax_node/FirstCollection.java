package com.github.namazu.syntax_node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.namazu.core.LexicalType;
import com.github.namazu.core.LexicalUnit;

/**
 * First集合のコレクション.
 *
 */
public class FirstCollection {
    List<LexicalType> firstListUnit;

    public FirstCollection(LexicalType... firstTypes) {
        this.firstListUnit = Arrays.asList(firstTypes);
    }

    public FirstCollection(FirstCollection... firstCollections) {
        this.firstListUnit = new ArrayList<>();
        for (FirstCollection firstCollection : firstCollections) {
            firstListUnit.addAll(firstCollection.firstListUnit);
        }
    }

    public boolean contains(LexicalUnit unit) {
        LexicalType type = unit.getType();
        return firstListUnit.contains(type);
    }
}
