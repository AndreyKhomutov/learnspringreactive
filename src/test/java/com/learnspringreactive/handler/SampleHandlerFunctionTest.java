package com.learnspringreactive.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
//@WebFluxTest TODO не подхожит потому что не сканирует пакеты на @Component и будет 404
@SpringBootTest
@AutoConfigureWebTestClient //TODO это надо добавлять когда нет WebFluxTest
@DirtiesContext //only for text when you change app context
public class SampleHandlerFunctionTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void flux_approach1() {
        Flux<Integer> integerFlux = webTestClient.get().uri("/functional/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux.log())
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

//    @Test
//    public void mono(){
//        Integer expected = Integer.valueOf(1);
//
//        webTestClient.get().uri("/functional/mono")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Integer.class)
//                .consumeWith((consumer) -> {
//                    assertEquals(expected, consumer.getResponseBody() );
//                });
//    }

}
