package br.com.poc.multthread.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PocSpringMultThreadService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PocSpringMultThreadService.class);


	@Async
	public CompletableFuture<List<String>> saveCars() throws Exception {
//		final long start = System.currentTimeMillis();

		List<String> cars = getListString();

//		LOGGER.info("Time Thread: {}", start);
		return CompletableFuture.completedFuture(cars);
	}

	private List<String> getListString() throws Exception {
		final List<String> cars = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			String uuid = UUID.randomUUID().toString();
			cars.add(uuid);		
			Thread.sleep(5);
//			LOGGER.info("Item {} - {}", i, uuid);

		}
		System.out.print(" " + Thread.currentThread().getName());
		return cars;
	}

}
