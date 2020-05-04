package com.learnspringreactive.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void filterTest(){
        Flux<String> stringFlux = Flux.fromIterable(names)
                .log()
                .filter(name -> name.contains("j"))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("jack", "jenny")
                .verifyComplete();
    }

}
