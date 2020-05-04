package com.learnspringreactive.repository;

import com.learnspringreactive.document.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(new Item(null, "Samsung TV", 400.0),
            new Item(null, "Lg TV", 240.00),
            new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beat", 149.99),
            new Item("ABC", "HeadPhones", 149.99)
    );

    @BeforeEach
    public void setUp(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item -> {
                    System.out.println("Inserted item is: " + item);
                }))
                .blockLast(); //дождаться завершения чтобы все вставилось
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll()) //0
                .expectSubscription()
                .expectNextCount(itemList.size())
                .verifyComplete();
    }

    @Test
    public void getItemByID(){
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches((item -> item.getDescription().equals("HeadPhones")))
                .verifyComplete();
    }

}
