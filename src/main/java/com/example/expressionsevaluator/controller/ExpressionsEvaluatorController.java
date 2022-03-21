package com.example.expressionsevaluator.controller;

import com.example.expressionsevaluator.model.EvaluatedValue;
import com.example.expressionsevaluator.model.NamedExpression;
import com.example.expressionsevaluator.services.ExpressionEvaluateService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/expressions")
@AllArgsConstructor
public class ExpressionsEvaluatorController {

    private final ExpressionEvaluateService service;

    @PostMapping
    public void saveFormula(@RequestBody NamedExpression expression) {
        service.save(expression);
    }

    @PostMapping("/{expressionName}/calculations")
    public EvaluatedValue evaluate(@PathVariable String expressionName, @RequestBody Map<String, String> variables) {
        return service.evaluate(expressionName, variables);
    }
}
