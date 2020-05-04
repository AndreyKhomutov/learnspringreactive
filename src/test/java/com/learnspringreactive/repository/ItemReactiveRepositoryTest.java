package com.learnspringreactive.repository;

import com.learnspringreactive.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext //only for text when you change app context
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
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item -> {
                    System.out.println("Inserted item is: " + item);
                }))
                .blockLast(); //wait all items will be added only FOR TESTING
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll()) //0
                .expectSubscription()
                .expectNextCount(itemList.size())
                .verifyComplete();
    }

    @Test
    public void getItemByID() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches((item -> item.getDescription().equals("HeadPhones")))
                .verifyComplete();
    }

    @Test
    public void findItemByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("HeadPhones").log("findByDescription :"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        Item newEntity = new Item(null, "Laptop Sony", 350.00);

        Mono<Item> savedItem = itemReactiveRepository.save(newEntity);

        StepVerifier.create(savedItem)
                .expectSubscription()
                .expectNextMatches((item -> item.getDescription().equals("Laptop Sony")))
                .verifyComplete();

    }

    @Test
    public void updateItem() {
        double finalPrice = 500.00;

        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("Lg TV")
                .map(item -> {
                    item.setPrice(finalPrice); //setNewPrice
                    return item;
                })
                .flatMap(item -> {
                    return itemReactiveRepository.save(item); //save new Item
                });

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches((item -> item.getPrice() == 500.00))
                .verifyComplete();
    }

    @Test
    public void deleteById() {
        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC") //Mono<Item>
                .map(Item::getId)//getId
                .flatMap(id -> {
                    return itemReactiveRepository.deleteById(id);
                });

        StepVerifier.create(deletedItem.log("deleteted : "))
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("new item list: "))
                .expectSubscription()
                .expectNextCount(itemList.size() - 1)
                .verifyComplete();
    }

    @Test
    public void deleteItem() {
        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("HeadPhones") //Mono<Item>
                .flatMap(item -> {
                    return itemReactiveRepository.delete(item);
                });

        StepVerifier.create(deletedItem.log("deleteted : "))
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("new item list: "))
                .expectSubscription()
                .expectNextCount(itemList.size() - 1)
                .verifyComplete();
    }

}
