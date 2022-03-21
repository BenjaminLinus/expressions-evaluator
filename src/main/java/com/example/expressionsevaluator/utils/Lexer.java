package com.example.expressionsevaluator.utils;

import com.example.expressionsevaluator.exceptions.ExpressionsEvaluatorException;
import com.example.expressionsevaluator.model.ArgumentTokens;
import com.example.expressionsevaluator.model.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.expressionsevaluator.model.TokenType.*;
import static java.util.Collections.emptyList;

public class Lexer {

    public static final String ERROR = "Not a character for tokens";

    private final String expression;

    private final int length;

    private int index;

    public Lexer(String expression) {
        this.expression = expression;
        length = expression.length();
    }

    private boolean ended() {
        return length <= index;
    }

    private char next() {
        return expression.charAt(index++);
    }

    private void skipSpaces() {
        while (!ended() && Character.isWhitespace(currentChar())) {
            next();
        }
    }

    private char currentChar() {
        return expression.charAt(index);
    }

    private boolean digitStarted(char c) {
        return Character.isDigit(c) || ('-' == c && index == 0);
    }

    private boolean functionStarted(char c) {
        return '@' == c;
    }

    private boolean signStarted(char c) {
        return c == '=' || c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean parenthesizedExpressionStarted(char c) {
        return '(' == c;
    }

    private boolean variableStarted(char c) {
        return Character.isAlphabetic(c);
    }

    private Token nextToken() {
        skipSpaces();
        if (ended()) {
            return null;
        } else if (digitStarted(currentChar())) {
            return nextDigit();
        } else if (signStarted(currentChar())) {
            return new Token(SIGN, Character.toString(next()));
        } else if (functionStarted(currentChar())) {
            return function();
        } else if (parenthesizedExpressionStarted(currentChar())) {
            return parenthesizedExpression();
        } else if (variableStarted(currentChar())) {
            return nextVariable();
        } else {
            throw new ExpressionsEvaluatorException(ERROR);
        }
    }

    private Token parenthesizedExpression() {
        StringBuilder b = new StringBuilder();
        int depth = 1;
        next();
        while (!ended() && (depth > 0)) {
            char next = next();
            if (next == '(') {
                ++depth;
                b.append(next);
            } else if (next == ')') {
                --depth;
                if (depth != 0) {
                    b.append(next);
                }
            } else {
                b.append(next);
            }
        }
        String expression = b.toString();
        return new Token(
                PARENTHESIZED_EXPRESSION,
                expression,
                "",
                emptyList(),
                new Lexer(expression).tokenize()
        );
    }

    private Token nextVariable() {
        StringBuilder builder = new StringBuilder();
        builder.append(next());
        while (!ended() && (Character.isAlphabetic(currentChar()) || Character.isDigit(currentChar()))) {
            builder.append(next());
        }
        return new Token(VARIABLE, builder.toString());
    }

    private Token function() {
        StringBuilder builder = new StringBuilder();
        StringBuilder args = new StringBuilder();
        int depth = 0;
        char next = ' ';
        String functionName = "";
        while (!ended() && !(next == ')' && depth == 0)) {
            next = next();
            if (next == '(') {
                if (depth == 0) {
                    functionName = builder.toString();
                }
                ++depth;
            } else if (next == ')') {
                --depth;
            }
            if (depth > 0 && !(depth == 1 && next == '(')) {
                args.append(next);
            }
            builder.append(next);
        }
        return new Token(
                FUNCTION,
                builder.toString().replace("@", ""),
                functionName.replace("@", ""),
                Arrays.stream(args.toString().split(","))
                        .map(this::createTokensList).collect(Collectors.toList()),
                Collections.emptyList()
        );
    }

    private ArgumentTokens createTokensList(String s) {
        try {
            return new ArgumentTokens(new Lexer(s).tokenize());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArgumentTokens();
        }
    }

    private Token nextDigit() {
        StringBuilder builder = new StringBuilder();
        builder.append(next());
        while (!ended() && Character.isDigit(currentChar())) {
            builder.append(next());
        }
        return new Token(DIGIT, builder.toString());
    }

    public List<Token> tokenize() {
        List<Token> tokensList = new ArrayList<>();
        Token t = nextToken();
        while (t  != null) {
            tokensList.add(t);
            t = nextToken();
        }
        return tokensList;
    }
}
