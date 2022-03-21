package com.example.expressionsevaluator.utils;

import com.example.expressionsevaluator.exceptions.ExpressionsEvaluatorException;
import com.example.expressionsevaluator.model.Node;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static com.example.expressionsevaluator.utils.TreeBuilder.*;
import static java.lang.Math.*;
import static java.math.BigDecimal.valueOf;

public record Evaluator(Map<String, BigDecimal> variables) {
    private static final String UNKNOWN_SIGN = "Unknown sign!";
    public static final String ERROR = "Expression error";
    private static final String NO_FUNCTION = "No such function ";
    private static final String VALUE_ERROR = "Value error";

    public Object expression(Node node) {
        return switch (node.type()) {
            case DIGIT -> digit(node);
            case PARENTHESIZED_EXPRESSION, SIGN -> evaluate(node);
            case VARIABLE -> variableName(node);
            case FUNCTION -> evalFunction(node);
            default -> throw new ExpressionsEvaluatorException(ERROR);
        };
    }

    private BigDecimal digit(Node token) {
        return new BigDecimal(token.value());
    }

    private BigDecimal evalFunction(Node node) {
        return valueOf(eval(node));
    }

    private double eval(Node node) {
        return switch (node.functionName()) {
            case "sqrt" -> sqrt(evaluate(node.getFunctionArgumentsTrees().get(0)).doubleValue());
            case "log" -> log(evaluate(node.getFunctionArgumentsTrees().get(0)).doubleValue());
            case "abs" -> abs((evaluate(node.getFunctionArgumentsTrees().get(0))).doubleValue());
            case "acos" -> acos((evaluate(node.getFunctionArgumentsTrees().get(0))).doubleValue());
            case "cos" -> cos((evaluate(node.getFunctionArgumentsTrees().get(0))).doubleValue());
            case "cosh" -> cosh((evaluate(node.getFunctionArgumentsTrees().get(0))).doubleValue());
            case "pow" -> pow(
                    evaluate(node.getFunctionArgumentsTrees().get(0)).doubleValue(),
                    evaluate(node.getFunctionArgumentsTrees().get(1)).doubleValue()
            );
            case "addExact" -> addExact(
                    evaluate(node.getFunctionArgumentsTrees().get(0)).intValue(),
                    evaluate(node.getFunctionArgumentsTrees().get(1)).intValue()
            );
            default -> throw new ExpressionsEvaluatorException(NO_FUNCTION + node.functionName());
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
            throw new ExpressionsEvaluatorException(VALUE_ERROR);
        }
    }

    private String variableName(Node token) {
        return token.value();
    }

    public BigDecimal evaluate(Node node) {
        if (node.getLeft() == null && node.getRight() == null) {
            return value(expression(node));
        } else {
            BigDecimal left = value(expression(node.getLeft()));
            BigDecimal right = value(expression(node.getRight()));
            return switch (node.value()) {
                case PLUS -> left.add(right);
                case MINUS -> left.subtract(right);
                case MULTIPLY -> left.multiply(right);
                case DIVIDE -> left.divide(right, 2, RoundingMode.HALF_UP);
                default -> throw new ExpressionsEvaluatorException(UNKNOWN_SIGN);
            };
        }
    }
}