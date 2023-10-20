package dfs_server;

import DAO.fileDAO;
import entities.publicFile;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Aryan Mehta
 */
public class Server extends Thread {

    private ServerSocket ss;
    private ArrayList<publicFile> fileList;

    private fileDAO fdAO;
    private static final int PORT_NUM = 9988;

    private HashMap<publicFile, ReentrantReadWriteLock> fileLocks;

    public Server() {
        fdAO = new fileDAO();
        fileLocks = new HashMap<>();
        fileList = fdAO.getAllFiles();
    }

    @Override
    public void run() {
        try {
            initFileLocks();
            System.out.println("File Locks Created!!");
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

    private void initFileLocks() {
        for (publicFile file : fileList) {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            fileLocks.put(file, lock);
        }
    }

    class clientThread implements Runnable {

        private Socket clientSocket;
        private String clientName;
        private String command = "";
        private boolean status = true;

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

                //fileList = fd.getAllFiles();
                data_out.writeInt(fileList.size());

                for (publicFile fld : fileList) {
                    byte[] srlF = fd.serializeObject(fld);
                    //send the length
                    data_out.writeInt(srlF.length);
                    data_out.write(srlF);
                }

                while (status) {
                    command = data_in.readUTF();

                    if (command.equals("READ_FILE_INIT")) {
                        handleReadLock();
                    } else if (command.equals("WRITE_FILE_INIT")) {
                        handleWriteLock();
                    }
                }

            } catch (SocketException se) {
                System.out.println(clientName + " Disconnected!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void shutDown() {
            try {
                data_in.close();
                data_out.close();
                clientSocket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private ReentrantReadWriteLock getFileLocks(String fileName) {
            ReentrantReadWriteLock l;
            for (publicFile f : fileList) {
                if (f.getFileName().equals(fileName)) {
                    l = fileLocks.get(f);
                    return l;
                }
            }
            return null;
        }

        private void handleReadLock() throws IOException,InterruptedException {
            String req_fileName = data_in.readUTF();
            ReentrantReadWriteLock fLock = getFileLocks(req_fileName);

            Lock readLock = fLock.readLock();

            //boolean lStatus = writeLock.tryLock();

            //check write lock
            //write lock is already aquired
                if (fLock.isWriteLocked()) {
                    data_out.writeUTF("FILE_ALREADY_LOCKED");
                    System.out.println("Lock Denied!");
                } else {
                    //aquire the lock
                    readLock.lock();
                    System.out.println("Locked!");
                    data_out.writeUTF("FILE_LOCK_AVAILABLE");
                }
        }

        private void handleWriteLock() throws IOException {
            String req_fileName = data_in.readUTF();

            ReentrantReadWriteLock flock = getFileLocks(req_fileName);

            Lock readLock = flock.readLock();
            Lock writeLock = flock.writeLock();

            //either read or write lock is taken
            if ((!readLock.tryLock()) || (!writeLock.tryLock())) {
                data_out.writeUTF("FILE_ALREADY_LOCKED");
            } else {
                writeLock.lock();
                data_out.writeUTF("FILE_LOCK_AQUIRED");
            }
        }
    }
}
