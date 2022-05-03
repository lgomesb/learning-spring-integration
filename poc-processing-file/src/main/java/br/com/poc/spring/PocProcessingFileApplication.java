package br.com.poc.spring;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.mvc.LastModified;

import br.com.poc.spring.service.FileService;

@SpringBootApplication
public class PocProcessingFileApplication implements CommandLineRunner {
	
	private static final String FILE_SOURCE_PATH = "source";

	final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private FileService fileService;
	
	public static void main(String[] args) {
		SpringApplication.run(PocProcessingFileApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

//		Resource resource = new ClassPathResource("data.txt");
		Resource resource = new ClassPathResource(FILE_SOURCE_PATH);
		LOGGER.info(resource.getURI().getPath());
		List<String> Files = this.listFileUsingFilesList(resource.getURI().getPath());

		for (String fileName : Files) {			
			fileService.uploadByThread( new ClassPathResource( FILE_SOURCE_PATH + File.separator + fileName));
		}
		
		
//		
//		try( InputStream inputStream = resource.getInputStream() )  {
//			
////			int lines = getLineNum(inputStream);
////			fileService.uploadByThread( (ClassPathResource) resource);
//			
//			LOGGER.info(Files.toString());
//			
////			byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
////			String data = new String(bdata, StandardCharsets.UTF_8);
////			LOGGER.info(data);
////			LOGGER.info(resource.getURI().getPath());
////			LOGGER.info("Lines: {}", lines);
//			
//		} catch (Exception e) {
//		      LOGGER.error("IOException", e);
//		}
		
		SpringApplication.exit(context, () -> 0);
	}

	
	public List<String> listFileUsingFilesList(String dir) throws IOException { 
		
		File fileDir = new File(dir);
		List<File> files = Arrays.asList(fileDir.listFiles());
		files.sort(Comparator.comparingLong(File::lastModified));
		
		files.stream().filter(f -> !f.isDirectory())
		.map(File::getName)
		.collect(Collectors.toList())
		 ;
		
		
		try( Stream<Path> stream = Files.list(Paths.get(dir)) ) {
			return stream
					.filter(file -> !Files.isDirectory( file ) )
					.map(Path::getFileName)
					.map(Path::toString)
					.sorted()
					.collect(Collectors.toList());
		}
		
	}

}
