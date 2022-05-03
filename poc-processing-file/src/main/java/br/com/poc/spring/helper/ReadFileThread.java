package br.com.poc.spring.helper;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





public class ReadFileThread implements Callable<List<String>> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadFileThread.class);
    private Integer startIndex;    //Number of lines the file started reading
    private Integer endIndex;      //End of file read lines
    private InputStream is;         //Input stream
    private String filePath; 		//Caminho do arquivo
    private FileOutputStream fout;
    
    public ReadFileThread(int startIndex, int endIndex, InputStream is, String filePath, FileOutputStream fout) throws Exception {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.is = is;
        this.filePath = filePath;
        this.fout = fout;
    }
	
	
	@Override
	public List<String> call() throws Exception {
		 long start = System.currentTimeMillis();
	        StringBuilder result = new StringBuilder();
	        List<String> resultList = new ArrayList<>();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
	        int loc = 1;
	        while (loc < startIndex) {
	            reader.readLine();
	            loc++;
	        }
	        while (loc < endIndex) {
//	            result.append(reader.readLine()).append("\r\n"); //  Read as string
	        	String strLine = reader.readLine().trim().substring(0, 12);
//	        	LOGGER.info("File: {} Line Value: {} " , this.filePath, strLine);
	        	fout.write(strLine.getBytes());
	        	fout.write(System.lineSeparator().getBytes());
	        	
	        	resultList.add(strLine);
	            loc++;
	        }
//	        result.append(reader.readLine());
	        resultList.add(result.toString());
//	        resultList.add(reader.readLine().trim());
//	        String strResult = result.toString();
//	        LOGGER.info("FILE -> {} :: RESULT:  {}", this.filePath, strResult);
	        reader.close();
	        is.close();
	        LOGGER.info("File ={} - Thread ={} Total time taken for file reading to complete={} Size={}",
	        		this.filePath,
	                Thread.currentThread().getName(), (System.currentTimeMillis() - start), loc);
	        return resultList;
	}

}
