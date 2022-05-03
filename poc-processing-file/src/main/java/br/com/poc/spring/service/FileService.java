package br.com.poc.spring.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import br.com.poc.spring.domain.FileThreadVO;
import br.com.poc.spring.helper.ReadFileThread;
import br.com.poc.spring.service.exceptions.WebsiteBusinessException;

@Service("fileService")
public class FileService{
    //journal
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    
    @Value("${file.thread.num}")
    private Integer threadNum; //Number of threads

    @Resource(name = "asyncFilmService")
    private ThreadPoolTaskExecutor executor;  //Thread pool

    /**
     * Enable multiple threads to read files in segments
     * PS:If the number of file lines is less than the number of threads, it will cause thread waste
     * It is suitable for reading data message line by line
     * @return
     * @throws Exception 
     */
//    public List uploadByThread(MultipartFile file) throws Exception {
//        return uploadFile(file);
//    }

	public List uploadByThread(ClassPathResource file) throws Exception {
		
		if (!file.exists()) {
            return null;
        }
        
		file.isFile();
		
		
		InputStream is = file.getInputStream();		
        List<FileThreadVO> threadVOS = new ArrayList<>(threadNum); //Custom thread entity object
        //Allocate the number of read rows for the thread
        Integer lines = getLineNum(is);     //Total number of document rows
        Integer line;                       //Number of rows allocated per thread
        Integer startLine;                 //Number of lines from which the thread reads the file
        Integer endLine;                   //Number of end lines of file read by thread
        File fileProcessing = new File("data_" + System.currentTimeMillis() );
        fileProcessing.createNewFile();
        FileOutputStream fout = new FileOutputStream(fileProcessing);
        
        
        //Calculate the number of allocated lines according to the number of file lines and threads. It's a little cumbersome here and needs to be optimized
        if (lines < threadNum) {
            for (int i = 1; i <= lines; i++) {
                FileThreadVO fileThreadVO = new FileThreadVO();
                startLine = endLine = i;
                InputStream stream = file.getInputStream();

                ReadFileThread readFileThread = new ReadFileThread(startLine, endLine, stream, file.getPath(), fout);
                fileThreadVO.setStartLine(startLine);
                fileThreadVO.setIs(stream);
                fileThreadVO.setEndLine(endLine);
                fileThreadVO.setResult(executor.submit(readFileThread).get());
                threadVOS.add(fileThreadVO);
            }
        } else {
            for (int i = 1, tempLine = 0; i <= threadNum; i++, tempLine = ++endLine) {
                InputStream stream = file.getInputStream();
                FileThreadVO fileThreadVO = new FileThreadVO();
                Integer var1 = lines / threadNum;
                Integer var2 = lines % threadNum;
                line = (i == threadNum) ? (var2 == 0 ? var1 : var1 + var2) : var1;
                startLine = (i == 1) ? 1 : tempLine;
                endLine = (i == threadNum) ? lines : startLine + line - 1;

                ReadFileThread readFileThread = new ReadFileThread(startLine, endLine, stream, file.getPath(), fout);
                fileThreadVO.setStartLine(startLine);
                fileThreadVO.setIs(stream);
                fileThreadVO.setEndLine(endLine);
                fileThreadVO.setResult(executor.submit(readFileThread).get());
                threadVOS.add(fileThreadVO);
            }
        }
        List resultCompleteList = new ArrayList<>(); //Integrate read results from multiple threads
        threadVOS.forEach(record->{
            List<String> result = record.getResult();
            resultCompleteList.addAll(result);
        });

        boolean isComplete = false;
        if (resultCompleteList != null ) {
            //Check the number of lines. Since this project uses a condition of reading behavior, only the number of lines can be checked, and bytes can also be checked
            int i = resultCompleteList.size() - lines;
            if (i == 0) {
                isComplete = true;
            }
        }
        if (!isComplete) {
            logger.error(">>>>>====uploadByThread====>>>>>>File integrity verification failed!");
            fout.close();
            throw new WebsiteBusinessException("The file is incomplete!" + HttpStatus.INTERNAL_SERVER_ERROR.value());//Custom exception and error code
        } else {
        	fout.close();
            return resultCompleteList;
        }
	}

    /**
     * Get the number of file lines
     * @param is
     * @return
     * @throws IOException
     */
    public int getLineNum(InputStream is) throws IOException {
        int line = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while (reader.readLine() != null) {
            line++;
        }
        reader.close();
        is.close();
        return line;
    }
}
