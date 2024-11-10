package example.cashcard;

import com.laiszig.buildingrestapi.BuildingRestApiApplication;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.laiszig.buildingrestapi.cashcard.CashCard;
import com.laiszig.buildingrestapi.cashcard.CashCardRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = BuildingRestApiApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
/*
1. Bootstrap the entire application context for integration testing.
This is useful for testing the application's layers in a real environment, including controllers, services, repositories, and configurations.
2. The webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT option ensures that the server will start on a random, available port instead of the default port (typically 8080).
This helps prevent port conflicts during testing, especially when running multiple tests concurrently or on CI/CD pipelines where multiple instances might be active.
3. Enable HTTP client testing within the test class. By starting the server on a random port, it allows testing of HTTP endpoints using a real HTTP client (e.g., TestRestTemplate or WebTestClient),
making it possible to send HTTP requests to your application as if it were running in a production environment.
*/
public class CashCardApplicationTests {

    @Autowired // Form of Spring dependency injection, it's best used only in tests.
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        // Converts the response String into a JSON-aware object with lots of helper methods.
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    void shouldCreateANewCashCard() {
        CashCard newCashCard = new CashCard(44L, 250.00);
        // We don't provide an id because the db will create and manage it.
        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cashcards", newCashCard, Void.class);
        // We don't expect a CashCard to be returned, so Void response body.
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        // The origin server SHOULD send a 201 (Created) response

        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
        // the response should contain a Location header field that provides an identifier for the primary resource created
        ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCashCard, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        /* When a POST request results in the successful creation of a resource, such as a new CashCard,
        the response should include information for how to retrieve that resource.
        A URI in a Response Header named "Location"
         */

        // Add assertions such as these
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(250.00);

    }
}
