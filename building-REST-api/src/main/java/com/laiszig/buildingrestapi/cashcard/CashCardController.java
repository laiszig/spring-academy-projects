package com.laiszig.buildingrestapi.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Spring Component. Capable of handling HTTP requests.
@RequestMapping("/cashcards") // Indicates which address requests must have to access this Controller.
class CashCardController {

    @GetMapping("/{requestedId}") // Handler method. Requests that match cashcards/{requestedID} will be handled by this method
    public ResponseEntity<String> findById() {
        return ResponseEntity.ok("{}");
    }

}
