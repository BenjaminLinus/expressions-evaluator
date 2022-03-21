package com.example.expressionsevaluator.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
public class Node {

    private TokenType tokenType;
    private Node left;
    private Node right;
    private String value;
    private String functionName;
    private List<ArgumentTokens> functionArguments;
    public List<Node> functionArgumentsNodes;
    private List<Token> tokens;

    public Node(
            TokenType type,
            String value,
            Node left,
            Node right,
            String functionName,
            List<ArgumentTokens> functionArguments,
            List<Node> functionArgumentsTrees,
            List<Token> tokens
    ) {
        this.tokenType = type;
        this.left = left;
        this.right = right;
        this.value = value;
        this.functionName = functionName;
        this.functionArguments = functionArguments;
        this.functionArgumentsNodes = functionArgumentsTrees;
        this.tokens = tokens;
    }

    public Node(TokenType type, String value) {
        this(
                type,
                value,
                null,
                null,
                "",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }
}
