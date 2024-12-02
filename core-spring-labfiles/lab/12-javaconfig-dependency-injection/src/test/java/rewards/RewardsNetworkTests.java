package rewards;

import common.money.MonetaryAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RewardsNetworkTests {

    RewardNetwork rewardNetwork;

    @BeforeEach
    void setUp() {
        // Bootstrapping the application by having Spring create, configure and assemble all beans defined in the two configuration files
        ConfigurableApplicationContext applicationContext = SpringApplication.run(TestInfrastructureConfig.class);
        rewardNetwork = applicationContext.getBean(RewardNetwork.class);
        /* Spring ApplicationContext encapsulates the knowledge about which component implementations have been selected for a given environment
        By working with a bean through its interface you decouple yourself from implementation complexity and volatility.
        Don't ask the context for beans "internal" to the application.

        The RewardNetwork is the application’s entry-point, setting the boundary for the application defined by an easy-to-use public interface.
        Asking the context for an internal bean such as a repository or data source is questionable.
         */
    }

    @Test
    void testRewardForDining() {
        // create a new dining of 100.00 charged to credit card '1234123412341234' by merchant '123457890' as test input
        Dining dining = Dining.createDining("100.00", "1234123412341234", "1234567890");

        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
        RewardConfirmation confirmation = rewardNetwork.rewardAccountFor(dining);

        // assert the expected reward confirmation results
        assertNotNull(confirmation);
        assertNotNull(confirmation.getConfirmationNumber());

        // assert an account contribution was made
        AccountContribution contribution = confirmation.getAccountContribution();
        assertNotNull(contribution);

        // the account number should be '123456789'
        assertEquals("123456789", contribution.getAccountNumber());

        // the total contribution amount should be 8.00 (8% of 100.00)
        assertEquals(MonetaryAmount.valueOf("8.00"), contribution.getAmount());

        // the total contribution amount should have been split into 2 distributions
        assertEquals(2, contribution.getDistributions().size());

        // each distribution should be 4.00 (as both have a 50% allocation)
        assertEquals(MonetaryAmount.valueOf("4.00"), contribution.getDistribution("Annabelle").getAmount());
        assertEquals(MonetaryAmount.valueOf("4.00"), contribution.getDistribution("Corgan").getAmount());

    }
}
