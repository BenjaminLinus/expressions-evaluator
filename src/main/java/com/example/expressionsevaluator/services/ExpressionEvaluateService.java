package com.example.expressionsevaluator.services;

import com.example.expressionsevaluator.model.EvaluatedValue;
import com.example.expressionsevaluator.model.NamedExpression;

import java.util.Map;

public interface ExpressionEvaluateService {

    void save(NamedExpression expression);

    EvaluatedValue evaluate(String expressionName, Map<String, String> variables);
}
