package com.example.expressionsevaluator.utils;

import com.example.expressionsevaluator.model.Node;
import com.example.expressionsevaluator.model.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.of;

@SpringBootTest
class EvaluatorTest {

    @ParameterizedTest
    @MethodSource("expressions")
    public void fullTest(String expression, BigDecimal result) {
        List<Token> tokens = new Lexer(expression).tokenize();
        Node node = new TreeBuilder(tokens).buildExpression();
        HashMap<String, BigDecimal> map = variablesMap();
        Evaluator interpreter = new Evaluator(map);
        BigDecimal run = interpreter.evaluate(node);
        Assertions.assertEquals(result.doubleValue(), run.doubleValue(), .00001);
    }

    private static List<Arguments> expressions() {
        return asList(
            of("(3 + 4) * 5", valueOf(35)),
            of("(3 + 4) * 5 + 1 + 2 + 0 + 11 - (1 + 2)", valueOf(46)),
            of("(3 + 4) * 5 + 1 + 2 + 0 + 11 - (1 + 2 * 6 -3-1 - (2-1)*3)", valueOf(43)),
            of("(3 + 4) * 5 + 1 + 2 + 0 + var1 - (1 + 2 * var2 -3-1 - (2-1)*3)", valueOf(39)),
            of("(3 + 4) / var3 + 1 + 2 + 0 + var1 - (1 + 2 * var2 -3-1 - (2-1)/var4)", valueOf(4.33)),
            of("(3 + 4) / var3 + 1 + 2 + 0 + var1 - (1 + 2 * var2 -3-1 - (2-1)/var4) - @sqrt(var5)", valueOf(2.33)),
            of("(var5)", valueOf(4)),
            of("var1", valueOf(15)),
            of("@sqrt(1 +2*3 + var5) -@sqrt(4) + @sqrt(var1 + (var2 - 5)*2)", valueOf(6.3166247903554)),
            of("@sqrt(1 +2*3 + var5) - @pow(2, 3 - 1*0)", valueOf(-4.6833752096446)),
            of("@abs(-100)", valueOf(100)),
            of("-200", valueOf(-200)),
            of("@cosh(0)", valueOf(1)),
            of("@cos(0)", valueOf(1)),
            of("@acos(0)", valueOf(1.5707963267948966)),
            of("@log(10)", valueOf(2.302585092994046)),
            of("@addExact(10, var4)", valueOf(11))
        );
    }

    private HashMap<String, BigDecimal> variablesMap() {
        HashMap<String, BigDecimal> map = new HashMap<>();
        map.put("var1", valueOf(15));
        map.put("var2", valueOf(10));
        map.put("var3", valueOf(3));
        map.put("var4", valueOf(1));
        map.put("var5", valueOf(4));
        return map;
    }
}