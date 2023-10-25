package DAO;

import entities.publicFile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JTextArea;

/**
 *
 * @author Aryan Mehta
 */
public class fileDAO {

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

    public publicFile deserializeObject(byte[] bytes) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (publicFile) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void writeToFile(File file,String content){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file,false));
            
            bw.write(content);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void readFromFile(File file,JTextArea ta){
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line=br.readLine())!=null){
                ta.append(line+"\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
