package br.com.poc.multthread;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import br.com.poc.multthread.service.PocSpringMultThreadService;

@SpringBootApplication
public class PocSpringMultThreadApplication implements CommandLineRunner{

	@Autowired
	private PocSpringMultThreadService service;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	public static void main(String[] args) {
		SpringApplication.run(PocSpringMultThreadApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		for ( int i = 0; i < 10; i++) {
			CompletableFuture<List<String>> future = service.saveCars();
			System.out.print("Processing file-" + (i+1) + " ");
			
			while (!future.isDone() ) {
				Thread.sleep(100);
				System.out.print(".");
			}
			System.out.println();
		}
		
		
		System.out.println("<<<<<<<<< DONE >>>>>>>>>");
		
		SpringApplication.exit(applicationContext, () -> 0);
		
	}

}
