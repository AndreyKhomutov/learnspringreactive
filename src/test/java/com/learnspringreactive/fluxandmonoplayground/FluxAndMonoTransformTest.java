package com.learnspringreactive.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .map(String::toUpperCase)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();

    }

    @Test
    public void transformUsingMap_lenght() {
        Flux<Integer> stringFlux = Flux.fromIterable(names)
                .map(String::length)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();

    }

    @Test
    public void transformUsingMap_lenght_repeat() {
        Flux<Integer> stringFlux = Flux.fromIterable(names)
                .map(String::length)
                .repeat(1)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();

    }

    @Test
    public void transformUsingMap_filter() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(n -> n.length() > 4)
                .map(String::toUpperCase)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("JENNY")
                .verifyComplete();

    }

    @Test
    public void transformUsingFlatMap() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertTolist(s));
                })
                .log();//db val or external service call that return a flux s-> Flat<String>
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertTolist(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "new value");
    }

    @Test
    public void transformUsingFlatMap_usingParallel() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)//Flux<Flux<String>> (A,B) (C,D) (E,F)
                .flatMap(s ->
                        s.map(this::convertTolist).subscribeOn(parallel()))//Flux<List<Sting>>
                .flatMap(st -> Flux.fromIterable(st))//Flux<Sting>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_usingParallel__With_Order() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)//Flux<Flux<String>> (A,B) (C,D) (E,F)
//                .concatMap(s ->
//                        s.map(this::convertTolist).subscribeOn(parallel()))//Flux<List<Sting>>
                .flatMapSequential(s ->
                        s.map(this::convertTolist).subscribeOn(parallel()))//Flux<List<Sting>>
                .flatMap(st -> Flux.fromIterable(st))//Flux<Sting>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

}
