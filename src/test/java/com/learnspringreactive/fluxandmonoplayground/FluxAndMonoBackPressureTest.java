package com.learnspringreactive.fluxandmonoplayground;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void backPressureTest() {
        Flux<Integer> flux = Flux.range(1, 10)
                .log();
        StepVerifier.create(flux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {
        Flux<Integer> flux = Flux.range(1, 10)
                .log();
        flux.subscribe((element) -> System.out.println(element),
                (ex) -> System.err.println(ex.getMessage()),
                () -> System.out.println("Done"),
                subscription -> subscription.request(2));
    }

    @Test
    public void backPressure_cancel() {
        Flux<Integer> flux = Flux.range(1, 10)
                .log();
        flux.subscribe((element) -> System.out.println(element),
                (ex) -> System.err.println(ex.getMessage()),
                () -> System.out.println("Done"),
                subscription -> subscription.cancel());
    }

    @Test
    public void customised_backPressure() {
        Flux<Integer> flux = Flux.range(1, 10)
                .log();

        flux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println(value);
                if (value==4) {
                    cancel();
                }

                super.hookOnNext(value);
            }
        });
    }

}
