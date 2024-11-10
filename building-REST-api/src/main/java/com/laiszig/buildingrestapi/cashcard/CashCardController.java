package com.laiszig.buildingrestapi.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController // Spring Component. Capable of handling HTTP requests.
@RequestMapping("/cashcards") // Indicates which address requests must have to access this Controller.
class CashCardController {

    private final CashCardRepository cashCardRepository;

    CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}") // Handler method. Requests that match cashcards/{requestedID} will be handled by this method
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        //@PathVariable makes Spring Web aware of the requestedId supplied in the HTTP request and available for us to use in our handler method.
        if (requestedId.equals(99L)) {
            CashCard cashCard = new CashCard(99L, 123.45);
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb) {
        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        /*
        This is constructing a URI to the newly created CashCard.
        This is the URI that the caller can then use to GET the newly-created CashCard.
        savedCashCard.id is used as the identifier, matching the GET endpoint's specification.
         */
        return ResponseEntity.created(locationOfNewCashCard).build();
    }
}
