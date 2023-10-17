package dfs_server;

import DAO.fileDAO;
import entities.publicFile;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Aryan Mehta
 */
public class Server {

    private ServerSocket ss;
    private ArrayList<publicFile> fileList;

    private static final int PORT_NUM = 9988;

    public void startServer() {
        try {
            ss = new ServerSocket(PORT_NUM);
            System.out.println("Server Listening on:" + PORT_NUM);

            while (true) {
                Socket soc = ss.accept();
                System.out.println("Connection Established via :" + soc.getInetAddress());

                clientThread cl = new clientThread(soc);
                Thread th = new Thread(cl);
                th.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class clientThread implements Runnable {

        private Socket clientSocket;
        private String clientName;

        private DataInputStream data_in;
        private DataOutputStream data_out;
        private ObjectOutputStream obj_out;
        private ObjectInputStream obj_in;
        private fileDAO fd;

        public clientThread(Socket sc) {
            this.clientSocket = sc;
            fd = new fileDAO();
        }

        @Override
        public void run() {
            try {
                data_in = new DataInputStream(clientSocket.getInputStream());
                data_out = new DataOutputStream(clientSocket.getOutputStream());
                
                clientName = data_in.readUTF();
                System.out.println(clientName);
                
                fileList = fd.getAllFiles();
                
                data_out.writeInt(fileList.size());
                
                for(publicFile fld : fileList){
                    byte[] srlF = fd.serializeObject(fld);
                    //send the length
                    data_out.writeInt(srlF.length);
                    data_out.write(srlF);
                }
                
                data_in.close();
                data_out.close();
                clientSocket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
