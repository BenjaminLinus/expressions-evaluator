package com.example.expressionsevaluator.model;

import java.util.Collections;
import java.util.List;

public record Token(
        TokenType type,
        String value,
        String functionName,
        List<ArgumentTokens> functionArguments,
        List<Token> parenthesizedExpression
) {

    public Token(TokenType type, String value) {
        this(type, value, "", Collections.emptyList(), Collections.emptyList());
    }
}
