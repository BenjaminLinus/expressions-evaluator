package com.example.expressionsevaluator.services.impl;

import com.example.expressionsevaluator.model.EvaluatedValue;
import com.example.expressionsevaluator.model.NamedExpression;
import com.example.expressionsevaluator.repositories.ExpressionsRepository;
import com.example.expressionsevaluator.services.ExpressionEvaluateService;
import com.example.expressionsevaluator.utils.Evaluator;
import com.example.expressionsevaluator.utils.Lexer;
import com.example.expressionsevaluator.utils.TreeBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExpressionEvaluateServiceImpl implements ExpressionEvaluateService {

    private final ExpressionsRepository repository;

    @Override
    public void save(NamedExpression expression) {
        repository.save(expression);
    }

    @Override
    public EvaluatedValue evaluate(String expressionName, Map<String, String> variables) {
        Optional<NamedExpression> expression = repository.findById(expressionName);
        if (expression.isPresent()) {
            NamedExpression namedExpression = expression.get();
            return new EvaluatedValue(new Evaluator(buildVariablesMap(variables))
                    .evaluate(
                            new TreeBuilder(
                                    new Lexer(namedExpression.getExpression()).tokenize()).buildExpression()).toString());
        } else {
            return null;
        }
    }

    private Map<String, BigDecimal> buildVariablesMap(Map<String, String> variables) {
        return variables.entrySet()
                .stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), new BigDecimal(e.getValue())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}
