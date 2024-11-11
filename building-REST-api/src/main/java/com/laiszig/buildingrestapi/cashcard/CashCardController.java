package com.laiszig.buildingrestapi.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
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

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
        Page<CashCard> page = cashCardRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }
    /*
    The getSortOr() method provides default values for the page, size, and sort parameters. The default values come from two different sources:
    Spring provides the default page and size values (they are 0 and 20, respectively). A default of 20 for page size explains why all three of our Cash Cards were returned. Again: we didn't need to explicitly define these defaults. Spring provides them "out of the box".
    We defined the default sort parameter in our own code, by passing a Sort object to getSortOr():
    If any of the three required parameters are not passed to the application, then reasonable defaults will be provided.
    */
}

/*
When we add the Spring Security dependency to our application, security is enabled by default.
If we don't specify how authentication and authorization should be performed within our API, Spring Security completely locks down our API.
 */
