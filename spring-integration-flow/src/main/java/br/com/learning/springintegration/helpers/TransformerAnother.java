package br.com.learning.springintegration.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class TransformerAnother {

	private final Logger LOGGER = LoggerFactory.getLogger(TransformerAnother.class);

	public String transform(String filePath) throws IOException {

		String content = new String( Files.readAllBytes(Paths.get(filePath)) );
		LOGGER.info("FilePath: {}", filePath);
		LOGGER.info("ContentFile: {}", content);
		
		return "TransformedAnother: " + content;
	}
	
}
