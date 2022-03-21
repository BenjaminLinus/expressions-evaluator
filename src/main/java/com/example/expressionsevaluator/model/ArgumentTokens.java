package com.example.expressionsevaluator.model;

import java.util.Collections;
import java.util.List;

public record ArgumentTokens(List<Token> tokens) {

    public ArgumentTokens() {
        this(Collections.emptyList());
    }
}
