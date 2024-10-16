package co.com.bancolombia.api;

import co.com.bancolombia.usecase.calculator.CalculatorUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {ApiRest.class, ApiRestTest.TestConfig.class})
@WebFluxTest
class ApiRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testEvaluateMathExpression() {
        Mono<String> producer = Mono.just("{\"expression\":\"b + 2.1 * (3 - 1) - a\",\"variables\":{\"a\":2,\"b\":10}}");
        webTestClient.post()
                .uri("/api/calculator")
                .contentType(MediaType.APPLICATION_JSON)
                .body(producer, String.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .value(userResponse -> assertThat(userResponse).isEqualTo("12.2"));
    }

    @Test
    void shouldValidateInputs() {
        Mono<String> producer = Mono.just("{\"expression\":\"process.ENV\",\"variables\":{}}");
        String expected = "\"Invalid expression or unsupported operator: 'process.ENV' Supported operators are: +, -, *, /, numbers, replaceable variables, parentheses and dot. For example: b + 2.1 * (3 - 1) - a\"";
        webTestClient.post()
                .uri("/api/calculator")
                .contentType(MediaType.APPLICATION_JSON)
                .body(producer, String.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .value(userResponse -> assertThat(userResponse).isEqualTo(expected));
    }

    @Test
    void testHello() {
        webTestClient.get()
                .uri("/api/hello")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .value(userResponse -> assertThat(userResponse).isEqualTo("Hello null"));
    }


    @Configuration
    static class TestConfig {
        @Bean
        public CalculatorUseCase calculatorUseCase() {
            return new CalculatorUseCase();
        }
    }

}
