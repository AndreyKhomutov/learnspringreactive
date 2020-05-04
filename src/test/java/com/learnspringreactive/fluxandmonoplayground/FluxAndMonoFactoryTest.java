package com.learnspringreactive.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void fluxUsingIterable(){
        Flux<String> stringFlux = Flux.fromIterable(names)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray(){
        String[] namesArray = new String[]{"adam", "anna", "jack", "jenny"};

        Flux<String> stringFlux = Flux.fromArray(namesArray)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();

    }

    @Test
    public void fluxUsingStream(){
        Flux<String> stringFlux = Flux.fromStream(names.stream())
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty(){
        Mono<String> stringMono = Mono.justOrEmpty(null);
        StepVerifier.create(stringMono.log())
                .verifyComplete();

    }

    @Test
    public void monoUsingSupplier(){
        Supplier<String> stringSupplier = () -> "adam";

        System.out.println(stringSupplier.get());

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);
        StepVerifier.create(stringMono.log())
                .expectNext("adam")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange(){
        Flux<Integer> integerFlux =  Flux.range(1,5);
        StepVerifier.create(integerFlux)
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

}
