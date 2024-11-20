package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewards.internal.RewardNetworkImpl;
import rewards.internal.account.AccountRepository;
import rewards.internal.account.JdbcAccountRepository;
import rewards.internal.restaurant.JdbcRestaurantRepository;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.JdbcRewardRepository;
import rewards.internal.reward.RewardRepository;

import javax.sql.DataSource;

@Configuration //The @Configuration tells Spring to treat this class as a set of configuration instructions to be used when the application is starting up.
public class RewardsConfig {

	// Set this by adding a constructor.
	private DataSource dataSource;

	public RewardsConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	/* Constructor injection: heavily used in modern Spring code.
	Constructor injection is favored over field injection:
	Easier to test.
	Safer than field injection, particularly when forcing immutability of injected members through final.
	Ability to decouple domain POJOs from Spring.
	*/

	//We need to build bean factories for each Spring bean you will use in your application
	@Bean
	public RewardNetworkImpl rewardNetwork() {
		return new RewardNetworkImpl(accountRepository(), restaurantRepository(), rewardRepository());
	}

	@Bean
	public AccountRepository accountRepository() {
		JdbcAccountRepository repository = new JdbcAccountRepository();
		repository.setDataSource(dataSource);
		return repository;
	}

	@Bean
	public RewardRepository rewardRepository() {
		JdbcRewardRepository repository = new JdbcRewardRepository();
		repository.setDataSource(dataSource);
		return repository;
	}

	@Bean
	public RestaurantRepository restaurantRepository() {
		JdbcRestaurantRepository repository	= new JdbcRestaurantRepository();
		repository.setDataSource(dataSource);
		return repository;
	}

/*
For best practices, a bean’s name should describe the service it provides.
It should not describe implementation details.
For this reason, a bean’s name often corresponds to its interface.
*/

}
/*
While you will likely use Annotation-based Configuration (that you will see in the Component Scanning module),
Java Configuration is the most flexible option that carries the spirit of Factory or Builder type patterns.
You will see this recurring in Spring Boot Auto-configuration projects, as well as projects where you may want to create beans that you cannot annotate.
Such examples include 3rd party code, or legacy code you are not allowed to modify.
*/