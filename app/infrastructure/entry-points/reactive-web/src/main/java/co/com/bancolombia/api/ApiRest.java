package co.com.bancolombia.api;

import co.com.bancolombia.usecase.calculator.CalculatorUseCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ApiRest {
    private final CalculatorUseCase useCase;


    @PostMapping(path = "/calculator")
    public Mono<Object> evaluateMathExpression(@RequestBody MathExpression expression) {
        return Mono.fromSupplier(() -> useCase.evaluate(expression.getExpression(), expression.getVariables()))
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(e.getMessage()));
    }


    @GetMapping(path = "/hello")
    public Mono<String> hello() {
        return Mono.just("Hello " + System.getenv("OWNER"));
    }

    @Data
    public static class MathExpression {
        private String expression;
        private Map<String, Double> variables;
    }
}
