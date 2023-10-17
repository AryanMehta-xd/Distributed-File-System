package DAO;

import entities.publicFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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

    public void sendFile(Socket clientSoc, publicFile pfile) {

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
}
