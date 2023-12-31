package dfs_client;

import DAO.fileDAO;
import GUI_frames.frame_main;
import entities.publicFile;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private ScheduledExecutorService src;
    
    private String clientUsername;
    
    private boolean thread_sts=true;
    
    public ArrayList<publicFile> getArrL() {
        return arrL;
    }

    public Client(frame_main fr) {
        this.frame = fr;
        src = Executors.newSingleThreadScheduledExecutor();
    }

    public void setThread_sts(boolean thread_sts) {
        this.thread_sts = thread_sts;
    }

    public String getClientUsername() {
        return clientUsername;
    }
    
    @Override
    public void run() {
        try {
            socket = new Socket("192.168.0.105", 9988);

            data_in = new DataInputStream(socket.getInputStream());
            data_out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connection Established!!");

            //write Username to server
            data_out.writeUTF(frame.getClientUsername());
            this.clientUsername = frame.getClientUsername();
            
            getFileList();

            src.scheduleAtFixedRate(updateThread, 0, 1, TimeUnit.SECONDS);
            
            //start CRUD Operations
            while (!socket.isClosed());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutDown() {
        try {
            socket.close();
            data_in.close();
            data_out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int sendReadRequest(String fileName) {
        String response;
        try {
            data_out.writeUTF("READ_FILE_INIT");
            data_out.writeUTF(fileName);

            response = data_in.readUTF();
            if (response.equals("FILE_ALREADY_LOCKED")) {
                return 0;
            } else if (response.equals("FILE_LOCK_AVAILABLE")) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    public int sendWriteRequest(String fileName) {
        String response;
        try {
            data_out.writeUTF("WRITE_FILE_INIT");
            data_out.writeUTF(fileName);

            response = data_in.readUTF();
            if (response.equals("FILE_ALREADY_LOCKED")) {
                return 0;
            } else if (response.equals("FILE_LOCK_AQUIRED")) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int sendReadUnlockRequest(String fileName) {
        String response;
        try {
            data_out.writeUTF("UNLOCK_FILE_READ_INIT");
            data_out.writeUTF(fileName);

            response = data_in.readUTF();

            if (response.equals("FILE_READ_UNLOCKED")) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int sendNewFile(publicFile file) {
        String response;
        try {
            data_out.writeUTF("NEW_FILE_MODE");

            byte[] b = fd.serializeObject(file);

            //send length
            data_out.writeInt(b.length);
            data_out.write(b);

            response = data_in.readUTF();
            if (response.equals("FILE_RECEIVED")) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void userDisconnect() throws IOException {
        data_out.writeUTF("USER_DISCONNECT_INIT");
    }

    public void sendRefreshRequest() {
        try {
            arrL.clear();
            data_out.writeUTF("FILE_LIST_REFRESH");
            getFileList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFileList() throws IOException {
        int arrS = data_in.readInt();
        System.out.println(arrS);

        for (int i = 0; i < arrS; i++) {
            byte[] pack = new byte[data_in.readInt()];
            data_in.readFully(pack);

            arrL.add(fd.deserializeObject(pack));
        }

        frame.showFileList(arrL);
    }
    
    public int sendWriteUnlockRequest(String filename){
        String response;
        try {
            data_out.writeUTF("UNLOCK_FILE_WRITE_INIT");
            data_out.writeUTF(filename);
            
            response = data_in.readUTF();
            
            if(response.equals("FILE_WRITE_UNLOCKED")){
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int sendUpdatedFile(publicFile file){
        try {
            data_out.writeUTF("FILE_UPDATE_INIT");
            data_out.writeUTF(file.getFileName());
            
            byte[] fb = fd.serializeObject(file);
            
            data_out.writeInt(fb.length);
            data_out.write(fb);
            
            String resp = data_in.readUTF();
            if(resp.equals("UPDATE_SUCCESS")){
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private Thread updateThread = new Thread(()->{
        if(thread_sts){
            sendRefreshRequest();
        }
    });
    
    public int sendDeleteRequest(String filename){
        try {
            data_out.writeUTF("DELETE_FILE_INIT");
            data_out.writeUTF(filename); 
            
            String res = data_in.readUTF();
            if(res.equals("FILE_IN_USE")){
                return 2;
            }else if(res.equals("SUCCESS")){
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    } 
}
