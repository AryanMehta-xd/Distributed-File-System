package dfs_client;

import DAO.fileDAO;
import GUI_frames.frame_main;
import entities.publicFile;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Aryan Mehta
 */
public class Client extends Thread {

    private Socket socket;

    private DataInputStream data_in;
    private DataOutputStream data_out;
    private fileDAO fd = new fileDAO();
    private frame_main frame;
    private ArrayList<publicFile> arrL = new ArrayList<>();

    public ArrayList<publicFile> getArrL() {
        return arrL;
    }
    
    public Client(frame_main fr){
        this.frame = fr;
    }
    
    @Override
    public void run() {
        try {
            socket = new Socket("127.0.0.1", 9988);

            data_in = new DataInputStream(socket.getInputStream());
            data_out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connection Established!!");

            //write Username to server
            data_out.writeUTF(frame.getClientUsername());
            
            int arrS = data_in.readInt();
            System.out.println(arrS);
            
            
            for(int i=0;i<arrS;i++){
                byte[] pack = new byte[data_in.readInt()];
                data_in.readFully(pack);
                
                arrL.add(fd.deserializeObject(pack));
            }            
            
            frame.showFileList(arrL);
            
            //start CRUD Operations
            
            
            socket.close();
            data_in.close();
            data_out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
