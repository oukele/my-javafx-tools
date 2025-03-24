package com.oukele.mytools.model;


/**
 * @author oukele
 */
public class FindResult {
    private String filePath;

    public FindResult(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
