package com.example.expressionsevaluator.utils;

import com.example.expressionsevaluator.exceptions.ExpressionsEvaluatorException;
import com.example.expressionsevaluator.model.Node;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static java.lang.Math.*;
import static java.math.BigDecimal.valueOf;

public record Evaluator(Map<String, BigDecimal> variables) {

    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String MULTIPLY = "*";
    public static final String DIVIDE = "/";
    public static final String UNKNOWN_SIGN = "Unknown sign!";

    public Object expression(Node node) {
        return switch (node.getTokenType()) {
            case DIGIT -> digit(node);
            case PARENTHESIZED_EXPRESSION, SIGN -> evaluate(node);
            case VARIABLE -> variableName(node);
            case FUNCTION -> evalFunction(node);
            default -> throw new ExpressionsEvaluatorException("Expression error");
        };
    }

    private BigDecimal digit(Node token) {
        return new BigDecimal(token.getValue());
    }

    private BigDecimal evalFunction(Node node) {
        return valueOf(eval(node));
    }

    private double eval(Node node) {
        return switch (node.getFunctionName()) {
            case "sqrt" -> sqrt(evaluate(node.getFunctionArgumentsNodes().get(0)).doubleValue());
            case "log" -> log(evaluate(node.getFunctionArgumentsNodes().get(0)).doubleValue());
            case "abs" -> abs((evaluate(node.getFunctionArgumentsNodes().get(0))).doubleValue());
            case "acos" -> acos((evaluate(node.getFunctionArgumentsNodes().get(0))).doubleValue());
            case "cos" -> cos((evaluate(node.getFunctionArgumentsNodes().get(0))).doubleValue());
            case "cosh" -> cosh((evaluate(node.getFunctionArgumentsNodes().get(0))).doubleValue());
            case "pow" -> pow(
                    evaluate(node.getFunctionArgumentsNodes().get(0)).doubleValue(),
                    evaluate(node.getFunctionArgumentsNodes().get(1)).doubleValue()
            );
            case "addExact" -> addExact(
                    evaluate(node.getFunctionArgumentsNodes().get(0)).intValue(),
                    evaluate(node.getFunctionArgumentsNodes().get(1)).intValue()
            );
            default -> throw new ExpressionsEvaluatorException("No such function " + node.getFunctionName());
        };
    }

    public BigDecimal value(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Double) {
            return valueOf((Double) value);
        } else if (value instanceof String) {
            return variables.get((String) value);
        } else {
            throw new ExpressionsEvaluatorException("right value error");
        }
    }

    private String variableName(Node token) {
        return token.getValue();
    }

    public BigDecimal evaluate(Node node) {
        if (node.getLeft() == null && node.getRight() == null) {
            return value(expression(node));
        } else {
            BigDecimal left = value(expression(node.getLeft()));
            BigDecimal right = value(expression(node.getRight()));
            return switch (node.getValue()) {
                case PLUS -> left.add(right);
                case MINUS -> left.subtract(right);
                case MULTIPLY -> left.multiply(right);
                case DIVIDE -> left.divide(right, 2, RoundingMode.HALF_UP);
                default -> throw new ExpressionsEvaluatorException(UNKNOWN_SIGN);
            };
        }
    }
}