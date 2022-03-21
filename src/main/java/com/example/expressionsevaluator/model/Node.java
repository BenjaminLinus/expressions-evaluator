package com.example.expressionsevaluator.model;

import lombok.Data;

import java.util.List;

@Data
public class Node {

    private Node left;
    private Node right;
    private Token token;
    private List<Node> functionArgumentsTrees;

    public Node(
            Token token,
            Node left,
            Node right,
            List<Node> functionArgumentsTrees
    ) {
        this.token = token;
        this.left = left;
        this.right = right;
        this.functionArgumentsTrees = functionArgumentsTrees;
    }

    public TokenType type() {
        return token.type();
    }

    public String value() {
        return token.value();
    }

    public String functionName() {
        return token.functionName();
    }

    public List<ArgumentTokens> functionArguments() {
        return token.functionArguments();
    }

    public List<Token> parenthesizedExpression() {
        return token.parenthesizedExpression();
    }
}
