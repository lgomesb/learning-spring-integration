package br.com.poc.springintegration.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;

@Configuration
@EnableIntegration
public class SpringIntegrationConfig {

	private final String URI_DIRECTORY_READ = "/home/leandro/Projetos/git/poc/workspace/spring-integration/files/read";
	private final String URI_DIRECTORY_WRITE = "/home/leandro/Projetos/git/poc/workspace/spring-integration/files/write";
	
	@Bean
	@InboundChannelAdapter(value = "fileInputChannel", poller = @Poller(fixedDelay = "5000"))
	public FileReadingMessageSource fileReadingMessageSource() {
		CompositeFileListFilter<File> filter = new CompositeFileListFilter<>();
		filter.addFilter(new SimplePatternFileListFilter("*.txt"));
		FileReadingMessageSource readder = new FileReadingMessageSource();
		readder.setDirectory(new File(URI_DIRECTORY_READ));
		readder.setFilter(filter);
		
		return readder;
	}
	
	@Bean
	@ServiceActivator( inputChannel = "fileInputChannel")
	public FileWritingMessageHandler fileWritingMessageHandler() {
		FileWritingMessageHandler writter = new FileWritingMessageHandler(new File(URI_DIRECTORY_WRITE) );
		writter.setAutoCreateDirectory(true);
		writter.setExpectReply(false);
		return writter;
	}
}
