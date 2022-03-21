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

    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String MULTIPLY = "*";
    public static final String DIVIDE = "/";
    private static final List<TokenType> UNARY_OPERATIONS = asList(DIGIT, VARIABLE, PARENTHESIZED_EXPRESSION, FUNCTION);
    private static final TokenType BINARY = SIGN;
    private static final String MALFORMED = "Malformed expression.";
    private static final String NO_TOKEN = "No token with index ";

    private final List<Token> tokens;
    private final Map<String, Integer> priorities = new HashMap<>();
    private int index;

    public TreeBuilder(List<Token> tokens) {
        this.tokens = new ArrayList<>(tokens);
        this.tokens.add(new Token(EOB, "(eob)"));
        priorities.put(MULTIPLY, 2);
        priorities.put(DIVIDE, 2);
        priorities.put(PLUS, 1);
        priorities.put(MINUS, 1);
    }

    private Node check(Node token) {
        if (UNARY_OPERATIONS.contains(token.type())) {
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
                token,
                null,
                null,
                Collections.emptyList()
        );
    }

    private int priority(String value) {
        return priorities.getOrDefault(value, 0);
    }

    private Node assignLeftRight(Node node, Node operator) {
        if (BINARY.equals(operator.type())) {
            operator.setLeft(buildInnerNodes(node));
            int leftPriority = priority(operator.value());
            operator.setRight(buildExpression(leftPriority));
            return operator;
        } else {
            throw new ExpressionsEvaluatorException(MALFORMED);
        }
    }

    private Node newNode(Node left) {
        return new Node(
                left.getToken(),
                left.getLeft(),
                left.getRight(),
                buildArgumentsNodes(left)
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
        return switch (node.type()) {
            case PARENTHESIZED_EXPRESSION -> new TreeBuilder(node.parenthesizedExpression()).buildExpression(0);
            case FUNCTION -> newNode(node);
            default -> node;
        };
    }

    private List<Node> buildArgumentsNodes(Node left) {
        return left.functionArguments().stream()
                .map(tokens -> new TreeBuilder(tokens.tokens())
                        .buildExpression(0)).collect(Collectors.toList());
    }
}

