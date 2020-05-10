package com.learnspringreactive.controller.v1;

import com.learnspringreactive.constants.ItemConstansts;
import com.learnspringreactive.document.Item;
import com.learnspringreactive.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@SpringBootTest
@ActiveProfiles("test") //to prevent double inserted items, do not need from CommandLineRunner
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public List<Item> data() {
        return Arrays.asList(
                new Item(null, "Samsung TV test", 399.66),
                new Item(null, "LG TV test", 799.66),
                new Item(null, "Sony TV test", 199.66),
                new Item("ABC", "Philips TV test", 899.66)
        );
    }

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("InsertedItem from Test is : " + item))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        webTestClient.get().uri(ItemConstansts.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    public void getAllItems_approach2() {
        webTestClient.get().uri(ItemConstansts.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach(item -> {
                        assertTrue(item.getId() != null);
                    });
                });
    }

    @Test
    public void getAllItems_approach3() {

        Flux<Item> itemFlux = webTestClient.get().uri(ItemConstansts.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log("value from network: "))
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getOneItem() {
        webTestClient.get().uri(ItemConstansts.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 899.66);
    }

    @Test
    public void getOneItem_notFound() {
        webTestClient.get().uri(ItemConstansts.ITEM_END_POINT_V1.concat("/{id}"), "ABCD")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void creatItem() {
        Item item = new Item(null, "printer", 555.00);

        webTestClient.post().uri(ItemConstansts.ITEM_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("printer")
                .jsonPath("$.price").isEqualTo(555.00);
    }

    @Test
    public void deleteItem() {
        webTestClient.delete().uri(ItemConstansts.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

}
