package co.com.bancolombia.usecase.calculator;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;
import java.util.regex.Pattern;

@Log
public class CalculatorUseCase {
    private static final String VALID_EXPRESSION_REGEX = "^[0-9.()\\s+\\-*/]+$";
    private final ScriptEngine engine;

    public CalculatorUseCase() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("graal.js");
        assert engine != null;
    }

    @SneakyThrows
    public Object evaluate(String expression, Map<String, Double> variables) {
        for (Map.Entry<String, Double> entry : variables.entrySet()) {
            String variable = entry.getKey();
            Double value = entry.getValue();
            expression = expression.replace(variable, value.toString());
        }
        if (!isValidExpression(expression)) {
            throw new IllegalArgumentException("Invalid expression or unsupported operator: '" + expression +
                    "' Supported operators are: +, -, *, /, numbers, replaceable variables, parentheses and dot." +
                    " For example: b + 2.1 * (3 - 1) - a");
        }
        log.info("Evaluating expression: " + expression);
        return engine.eval(expression);
    }

    private boolean isValidExpression(String expression) {
        return Pattern.matches(VALID_EXPRESSION_REGEX, expression.replaceAll("\\s+", ""));
    }
}
