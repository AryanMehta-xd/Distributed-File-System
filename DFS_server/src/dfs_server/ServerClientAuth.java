package dfs_server;

import DAO.UserDAO;
import DAO.fileDAO;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author Aryan Mehta
 */
public class ServerClientAuth extends Thread {

    private ServerSocket ss;
    private UserDAO dao = new UserDAO();
    private fileDAO fd;
    
    @Override
    public void run() {
        try {
            fd = new fileDAO();
            ss = new ServerSocket(9987);
            System.out.println("Port Started on:9987");

            fd.addLog("AUTHENTICATION SERVER STARTED ON:9987");
            
            while (true) {
                Socket sco = ss.accept();
                clientAuth cl = new clientAuth(sco);
                System.out.println("User Connected!!");

                fd.addLog("SYSTEM CONNECTED ->"+sco.getInetAddress()+" VIA:9987");
                
                Thread th = new Thread(cl);
                th.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class clientAuth implements Runnable {

        private Socket clientSocket;

        private DataInputStream data_in;
        private DataOutputStream data_out;
        private String command = "";
        private boolean status = true;

        public clientAuth(Socket soc) {
            this.clientSocket = soc;
        }

        @Override
        public void run() {
            try {
                data_in = new DataInputStream(clientSocket.getInputStream());
                data_out = new DataOutputStream(clientSocket.getOutputStream());

                while (status) {
                    command = data_in.readUTF();

                    if (command.equals("NEW_USER_INIT")) {
                        String username = data_in.readUTF();
                        String password = data_in.readUTF();
                        String email = data_in.readUTF();

                        data_out.writeInt(dao.addUser(username, password, email));
                    } else if (command.equals("USER_AUTH_INIT")) {
                        String username = data_in.readUTF();
                        String password = data_in.readUTF();

                        System.out.println("User Auth Request!!");
                        fd.addLog(username+" AUTHENTICATION REQUEST VIA:9987");
                        
                        data_out.writeInt(dao.verifyUser(username, password));
                        fd.addLog("ACCOUNT CREATED ->SUCCESS");
                    }else if(command.equals("USER_DISCONNECT_INIT")){
                        System.out.println("User Disconnected!!");
                        fd.addLog("CLIENT DISCONNECTED ->"+clientSocket.getInetAddress()+" VIA:9987");
                        status=false;
                    }
                }
            } catch (SocketException | EOFException se) {
                System.out.println("Client Disconncted!!");
                fd.addLog("CLIENT DISCONNECTED ->"+clientSocket.getInetAddress()+" VIA:9987");
                shutDown();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        private void shutDown(){
            try {
                clientSocket.close();
                data_in.close();
                data_out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
