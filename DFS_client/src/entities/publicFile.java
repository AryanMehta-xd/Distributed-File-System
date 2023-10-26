package entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Aryan Mehta
 */
public class publicFile implements Serializable {

    private String fileData;
    private String file_Cr;
    private String fileName;
    private ArrayList<String> fileUpdates;

    private LocalDateTime localDateTime;
    
    public publicFile(String fileData, String file_Cr, String name) {
        this.fileData = fileData;
        this.file_Cr = file_Cr;
        this.fileName = name;
        fileUpdates = new ArrayList<>();
    }

    public ArrayList<String> getFileUpdates() {
        return fileUpdates;
    }

    public void setFileUpdates(ArrayList<String> fileUpdates) {
        this.fileUpdates = fileUpdates;
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

    public void addFileUpdate(String user){
        localDateTime = LocalDateTime.now();
        String fr = String.format("%02d-%02d-%02d %02d:%02d:%02d",
                localDateTime.getDayOfMonth(),localDateTime.getMonthValue(),localDateTime.getYear(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        fileUpdates.add(fr+" ->"+user);
    }
    
    public String getLastUpdate(){
        return fileUpdates.get((fileUpdates.size())-1);
    }
}
