package example.cashcard;

import com.laiszig.buildingrestapi.BuildingRestApiApplication;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.laiszig.buildingrestapi.cashcard.CashCard;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
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
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
/*
Sometimes tests interfere with new tests by creating a new object. @DirtiesContext fixes this problem by causing Spring to start with a clean state, as if those other tests hadn't been run.
Although you can use @DirtiesContext to work around inter-test interaction, you shouldn't use it indiscriminately;
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
    @DirtiesContext
    @Disabled
    void shouldCreateANewCashCard() {
        CashCard newCashCard = new CashCard(null, 250.00);
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

    @Test
    void shouldReturnAllCashCardsWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int cashCardCount = documentContext.read("$.length()");
        assertThat(cashCardCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.0, 150.00);
    }

    @Test
    void shouldReturnAPageOfCashCards() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnASortedPageOfCashCards() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray read = documentContext.read("$[*]");
        assertThat(read.size()).isEqualTo(1);

        double amount = documentContext.read("$[0].amount");
        assertThat(amount).isEqualTo(150.00);
    }
    /*
    If we don't specify a sort order, the cards are returned in the order they are returned from the database.
    And this happens to be the same as the order in which they were inserted.
    An important observation: Not all databases will act the same way. Now, it should make even more sense why we specify a sort order (instead of relying on the database's default order).
     */

    @Test
    void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactly(1.00, 123.45, 150.00);
    }
}
