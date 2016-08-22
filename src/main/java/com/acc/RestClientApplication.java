package com.acc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.reactive.ClientWebRequestBuilders;
import org.springframework.web.client.reactive.ResponseExtractors;
import org.springframework.web.client.reactive.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive REST client application to test with Reactive Account REST service 
 * @author vikash.kaushik
 */
@SpringBootApplication
public class RestClientApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(RestClientApplication.class, args);
		AccountRestClient client = ctx.getBean(AccountRestClient.class);
		
		client.getAccount(); 
		client.getAllAccounts(); 
//		client.createNewAccount(); 
	}
	
	@Bean public WebClient myWebClient(){
		ClientHttpConnector httpConnector = new ReactorClientHttpConnector();
		return new WebClient(httpConnector);
	}
}


@Component
class AccountRestClient{
	
	@Value("${get.account.id}")
	private String accountId;

	@Value("${get.account.newName}")
	private String newAccountName;
	
	@Value("${reactive.rest.url}")
	private String restUrl;
	
	private static final Logger log = LoggerFactory.getLogger(AccountRestClient.class);
	
	@Autowired private WebClient webClient;
	
	/**
	 * Calls Get account by account id using rest account service.
	 * application.properties has property as "get.account.id" for accountId 
	 */
	public void getAccount(){
		Mono<Account> response = webClient
				.perform(ClientWebRequestBuilders.get(restUrl+"account?accountId="+accountId).accept(MediaType.APPLICATION_JSON))
				.extract(ResponseExtractors.body(Account.class));
		
		response.log().subscribe(System.out::println);
	}

	/**
	 * Calls Get <i>ALL</i> accounts using rest account service.
	 */
	public void getAllAccounts(){
		Flux<Account> response = webClient
				.perform(ClientWebRequestBuilders.get(restUrl+"account/all").accept(MediaType.APPLICATION_JSON))
				.extract(ResponseExtractors.bodyStream(Account.class));
		
		response.log().subscribe(System.out::println);
	}
	
	/**
	 * TODO - Pending and working on it.
	 * Creates a new Account and find the same account by Name and update with Name+date
	 */
	public void createNewAccount(){
		Account newAccount = new Account(Long.valueOf(0), "New Name");
		//Create new account
		Mono<HttpHeaders> respHeaders = webClient.perform(ClientWebRequestBuilders.post(restUrl+"account/new", "{\"id\": 0, \"accountName\": \"New Name\" }")
														.accept(MediaType.APPLICATION_JSON)
														.contentType(MediaType.APPLICATION_JSON))
												.extract(ResponseExtractors.headers());
		respHeaders.log()
		.subscribe(System.out::println);
		
		
		//Find account by Name
		/*Flux<Account> response = webClient
				.perform(ClientWebRequestBuilders.get(restUrl+"account/like?accountName="+newAccount.getAccountName().split(" ")[1]).accept(MediaType.APPLICATION_JSON))
				.extract(ResponseExtractors.bodyStream(Account.class));
		//update Account with new Name
		response.log().flatMap(
				account -> {
					log.info("Found Created Account");
					System.out.println("Found Account: "+account.toString());
					String newName = account.getAccountName() + new Date().toString();
					account.setAccountName(newName);
					return Mono.just(account).subscribeOn(Schedulers.parallel());
				}
		).subscribe(account -> webClient.perform(ClientWebRequestBuilders.post(restUrl+"account/update", account).accept(MediaType.APPLICATION_JSON))
				.extract(ResponseExtractors.headers())
				.log().subscribe(System.out::println));*/
		
	}
}
