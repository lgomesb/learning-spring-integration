package br.com.learning.springintegration.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.messaging.MessageChannel;

import br.com.learning.springintegration.helpers.FileProcessor;
import br.com.learning.springintegration.helpers.Transformer;

@Configuration
public class IntegrationConfig {

	@Autowired
	private Transformer transformer;
	
	@Autowired
	private FileProcessor fileProcessor;
	
//	@Autowired
//	private TransformerAnother transformerAnother;

	@Bean
	public IntegrationFlow integrationFlow() {
		return IntegrationFlows
				.from(fileReader(), 
						spec -> spec.poller(Pollers.fixedDelay(500)))
				.transform(transformer, "transform")
				.handle(fileWriter())
				.to(anotherFlow())
				;
//		.get()
//		.handle(fileWriter())
	}
	
	@Bean
	public IntegrationFlow anotherFlow() {		
		return IntegrationFlows
				.from(fileReaderProcessing(), 
						spec -> spec.poller(Pollers.fixedDelay(500)))
				.transform(fileToStringTransformer())
				.handle(fileProcessor,"process")				
				.get()
				;
	}
	
	@Bean
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }
	
	@Bean
	public FileToStringTransformer fileToStringTransformer() {
	    return new FileToStringTransformer();
	}

	@Bean
	public FileReadingMessageSource fileReader() {
		FileReadingMessageSource source = new FileReadingMessageSource();
		source.setDirectory(new File("source"));
		
		return source;
	}

	@Bean
	public FileReadingMessageSource fileReaderProcessing() {
		FileReadingMessageSource source = new FileReadingMessageSource();
		source.setDirectory(new File("processing"));
		
		return source;
	}
	
	@Bean
	public FileWritingMessageHandler fileWriterFinish() {
		
		FileWritingMessageHandler handler = new FileWritingMessageHandler(new File("finished"));
		handler.setExpectReply(false);
		return handler;
	}


	@Bean
	public FileWritingMessageHandler fileWriter() {
		
		FileWritingMessageHandler handler = new FileWritingMessageHandler(new File("processing"));
		handler.setExpectReply(false);
		return handler;
	}

	

}
