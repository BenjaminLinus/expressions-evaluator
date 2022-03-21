package com.example.expressionsevaluator.utils;

import com.example.expressionsevaluator.exceptions.ExpressionsEvaluatorException;
import com.example.expressionsevaluator.model.Node;
import com.example.expressionsevaluator.model.Token;
import com.example.expressionsevaluator.model.TokenType;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.expressionsevaluator.model.TokenType.*;
import static java.util.Arrays.asList;

public class TreeBuilder {

    private static final List<TokenType> UNARY_OPERATIONS = asList(DIGIT, VARIABLE, PARENTHESIZED_EXPRESSION, FUNCTION);
    private static final TokenType BINARY = SIGN;
    public static final String MALFORMED = "Malformed expression.";
    public static final String NO_TOKEN = "No token with index ";

    private final List<Token> tokens;
    private final Map<String, Integer> priorities = new HashMap<>();
    private int index;

    public TreeBuilder(List<Token> tokens) {
        this.tokens = new ArrayList<>(tokens);
        this.tokens.add(new Token(EOB, "(eob)"));
        priorities.put("*", 60);
        priorities.put("/", 60);
        priorities.put("+", 50);
        priorities.put("-", 50);
        priorities.put("=", 10);
    }

    private Node check(Node token) {
        if (UNARY_OPERATIONS.contains(token.getTokenType())) {
            return token;
        } else {
            throw new ExpressionsEvaluatorException(MALFORMED);
        }
    }

    private Token current() {
        if (tokens.size() <= index) {
            throw new ExpressionsEvaluatorException(NO_TOKEN + index);
        }
        return tokens.get(index);
    }

    private Node next() {
        Token token = current();
        ++index;
        return new Node(
                token.type(),
                token.value(),
                null,
                null,
                token.functionName(),
                token.functionArguments(),
                Collections.emptyList(),
                token.tokens()
        );
    }

    private int priority(String value) {
        return priorities.getOrDefault(value, 0);
    }

    private Node assignLeftRight(Node node, Node operator) {
        if (BINARY.equals(operator.getTokenType())) {
            operator.setLeft(buildInnerNodes(node));
            int leftPriority = priority(operator.getValue());
            operator.setRight(buildExpression(leftPriority));
            return operator;
        } else {
            throw new ExpressionsEvaluatorException(MALFORMED);
        }
    }

    private Node newNode(Node left) {
        return new Node(
                left.getTokenType(),
                left.getValue(),
                left.getLeft(),
                left.getRight(),
                left.getFunctionName(),
                left.getFunctionArguments(),
                buildArgumentsNodes(left),
                left.getTokens()
        );
    }

    public Node buildExpression() {
        return buildExpression(0);
    }

    private Node buildExpression(int leftPriority) {
        Node leftNode = check(next());
        int rightPriority = priority(current().value());
        while (leftPriority < rightPriority) {
            leftNode = assignLeftRight(leftNode, next());
            rightPriority = priority(current().value());
        }
        return buildInnerNodes(leftNode);
    }

    private Node buildInnerNodes(Node node) {
        return switch (node.getTokenType()) {
            case PARENTHESIZED_EXPRESSION -> new TreeBuilder(node.getTokens()).buildExpression(0);
            case FUNCTION -> newNode(node);
            default -> node;
        };
    }

    private List<Node> buildArgumentsNodes(Node left) {
        return left.getFunctionArguments().stream()
                .map(tokens -> new TreeBuilder(tokens.tokens())
                        .buildExpression(0)).collect(Collectors.toList());
    }
}

