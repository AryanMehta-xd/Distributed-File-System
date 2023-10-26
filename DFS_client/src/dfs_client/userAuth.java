package dfs_client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 *
 * @author Aryan Mehta
 */
public class userAuth extends Thread {

    private DataInputStream data_in;
    private DataOutputStream data_out;
    private Socket socket;
    private boolean status = true;

    @Override
    public void run() {
        try {
            socket = new Socket("10.25.0.139", 9987);

            data_in = new DataInputStream(socket.getInputStream());
            data_out = new DataOutputStream(socket.getOutputStream());

            while (!socket.isClosed());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int newUser(String username, String password, String email) {
        try {
            data_out.writeUTF("NEW_USER_INIT");

            data_out.writeUTF(username);
            data_out.writeUTF(password);
            data_out.writeUTF(email);

            return data_in.readInt();
        } catch (Exception e) {
            e.printStackTrace();
            return 10;
        }
    }

    public int verifyUser(String username, String password) {
        try {
            data_out.writeUTF("USER_AUTH_INIT");

            data_out.writeUTF(username);
            data_out.writeUTF(password);

            status = false;
            return data_in.readInt();
        } catch (Exception e) {
            e.printStackTrace();
            return 10;
        }
    }

    public void disconnectUser(){
        try {
            data_out.writeUTF("USER_DISCONNECT_INIT");
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
    
    public void stopThread(){
        status=false;
    }
}
