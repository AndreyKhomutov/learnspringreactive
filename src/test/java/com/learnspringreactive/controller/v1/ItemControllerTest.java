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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

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
                new Item(null, "Philips TV test", 899.66)
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

}
