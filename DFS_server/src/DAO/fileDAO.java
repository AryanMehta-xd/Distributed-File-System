package DAO;

import entities.publicFile;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Aryan Mehta
 */
public class fileDAO {

    private static final String LOCAL_DIR = "DFS_files";
    ObjectOutputStream obj_out;
    ObjectInputStream obj_in;

    public void saveFile(publicFile pFile) {
        String filePath = LOCAL_DIR + File.separator + pFile.getFileName() + ".ser";

        try {
            obj_out = new ObjectOutputStream(new FileOutputStream(filePath));
            obj_out.writeObject(pFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<publicFile> getAllFiles() {
        ArrayList<publicFile> fileList = new ArrayList<>();
        File directory = new File(LOCAL_DIR);
        File[] allFiles = directory.listFiles((dir, name) -> name.endsWith(".ser"));
        try {
            if (allFiles != null) {
                for (File fl : allFiles) {
                    obj_in = new ObjectInputStream(new FileInputStream(fl));
                    fileList.add((publicFile) obj_in.readObject());
                }
                return fileList;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] serializeObject(publicFile object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public publicFile deserializeObject(byte[] bytes){
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (publicFile) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String getUpdateString(String update){
        LocalDateTime localDateTime = LocalDateTime.now();
        
        String fr = String.format("\n%02d-%02d-%02d\t%02d:%02d:%02d\t",
                localDateTime.getDayOfMonth(),localDateTime.getMonthValue(),localDateTime.getYear(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        
        return fr+update;
    }
    
    public void addLog(String log){
        try {
            File logFile = new File("server_log.txt");
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true));
            bw.write(getUpdateString(log));
            
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean deleteFile(String filename){
        String fp = LOCAL_DIR+File.separator+filename+".ser";
        
        File f = new File(fp);
        System.out.println(f.getName());
//        if(f.exists()){
//            return f.delete();
//        }else{
//            System.out.println("File not Found");
//        }
        return false;
    }
}
