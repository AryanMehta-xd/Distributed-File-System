package entities;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Aryan Mehta
 */
public class publicFile implements Serializable{
    private File local_file;
    private String file_Cr;
    private String fileName;
    private String lastUpdate;
    
    public publicFile(File local_file, String file_Cr) {
        this.local_file = local_file;
        this.file_Cr = file_Cr;
        this.fileName = local_file.getName();
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    public File getLocal_file() {
        return local_file;
    }

    public void setLocal_file(File local_file) {
        this.local_file = local_file;
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
