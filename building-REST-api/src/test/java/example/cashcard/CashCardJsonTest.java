package example.cashcard;

import com.laiszig.buildingrestapi.BuildingRestApiApplication;
import com.laiszig.buildingrestapi.cashcard.CashCard;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
/* JacksonTester is a convenience wrapper to the Jackson JSON parsing library.
It handles serialization and deserialization of JSON objects.
- Serialization is a mechanism of converting the state of an object into a byte stream.
- Deserialization is the reverse process of serialization. It transforms data from a file or byte stream back into an object for your application.
This makes it possible for an object serialized on one platform to be deserialized on a different platform.
For example, your client application can serialize an object on Windows while the backend would deserialize it on Linux.
Serialization and deserialization work together to transform/recreate data objects to/from a portable format.
The most popular data format for serializing data is JSON.
*/
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = BuildingRestApiApplication.class)
/* Marked as test class, using Jackson framework - part os Spring, extensive JSON testing and parsing support
Also stablishes all the related behavior to test JSON objects */
public class CashCardJsonTest {

    @Autowired
    // Autowired is an annotation that directs Spring to create an object of the requested type.
    private JacksonTester<CashCard> json;

    @Autowired
    private JacksonTester<CashCard[]> jsonList;

    private CashCard[] cashCards;

    @BeforeEach
    void setUp() {
        cashCards = Arrays.array(
                new CashCard(99L, 123.45),
                new CashCard(100L, 1.00),
                new CashCard(101L, 150.00));
    }

    @Test
    void cashCardSerializationTest() throws IOException {
        CashCard cashCard = cashCards[0];
        assertThat(json.write(cashCard)).isStrictlyEqualToJson("single.json");
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(99);
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount")
                .isEqualTo(123.45);
    }

    @Test
    void cashCardDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 99,
                    "amount": 123.45
                }
                """;
        assertThat(json.parse(expected))
                .isEqualTo(new CashCard(99L, 123.45));
        assertThat(json.parseObject(expected).id()).isEqualTo(99);
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }

    @Test
    void cashCardListSerializationTest() throws IOException {
        assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void cashCardListDeserializationTest() throws IOException {
        String expected="""
         [
            { "id": 99, "amount": 123.45 },
            { "id": 100, "amount": 1.00 },
            { "id": 101, "amount": 150.00 }
         ]
         """;
        assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
    }

}
