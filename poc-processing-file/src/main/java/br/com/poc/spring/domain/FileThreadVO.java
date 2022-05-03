package br.com.poc.spring.domain;

import java.io.InputStream;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileThreadVO<T> {

    private InputStream is;
    private Integer startLine;
    private Integer endLine;
    private String filePath;
    private List<T> result;
	
}
