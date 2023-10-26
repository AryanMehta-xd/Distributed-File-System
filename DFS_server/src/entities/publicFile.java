package entities;

import java.io.Serializable;

/**
 *
 * @author Aryan Mehta
 */
public class publicFile implements Serializable{
    private String fileData;
    private String file_Cr;
    private String fileName;
    private String lastUpdate;
    
    public publicFile(String fileData, String file_Cr,String name) {
        this.fileData = fileData;
        this.file_Cr = file_Cr;
        this.fileName = name;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public String getFile_Cr() {
        return file_Cr;
    }

    public void setFile_Cr(String file_Cr) {
        this.file_Cr = file_Cr;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    
}
