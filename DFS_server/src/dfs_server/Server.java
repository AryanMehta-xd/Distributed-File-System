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
import java.util.Map;
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

    private HashMap<String, ReentrantReadWriteLock> fileLocks;

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
            fileLocks.put(file.getFileName(), lock);
        }
    }

    class clientThread implements Runnable {

        private Socket clientSocket;
        private String clientName;
        private String command = "";
        private boolean status = true;

        private HashMap<clientThread,ReentrantReadWriteLock> heldLocks;
        
        private DataInputStream data_in;
        private DataOutputStream data_out;
        private ObjectOutputStream obj_out;
        private ObjectInputStream obj_in;
        private fileDAO fd;

        public clientThread(Socket sc) {
            this.clientSocket = sc;
            fd = new fileDAO();
            heldLocks = new HashMap<>();
        }

        @Override
        public void run() {
            try {
                data_in = new DataInputStream(clientSocket.getInputStream());
                data_out = new DataOutputStream(clientSocket.getOutputStream());

                clientName = data_in.readUTF();
                System.out.println(clientName);

                //fileList = fd.getAllFiles();
                sendFileList();

                while (status) {
                    command = data_in.readUTF();

                    switch (command) {
                        case "READ_FILE_INIT":
                            handleReadLock();
                            break;
                        case "WRITE_FILE_INIT":
                            handleWriteLock();
                            break;
                        case "UNLOCK_FILE_READ_INIT":
                            handleReadUnlockRequest();
                            break;
                        case "NEW_FILE_MODE":
                            handleNewFile();
                            break;
                        case "FILE_LIST_REFRESH":
                            sendFileList();
                            break;
                        case "UNLOCK_FILE_WRITE_INIT":
                            handleWriteUnlockRequest();
                            break;
                        case "FILE_UPDATE_INIT":
                            handleFileUpdateRequest();
                            break;
                        default:
                            break;
                    }
                }

            } catch (SocketException se) {
                System.out.println(clientName + " Disconnected!!");
                unlockAllFiles();
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
            return fileLocks.get(fileName);
        }

        private void handleReadLock() throws IOException, InterruptedException {
            String req_fileName = data_in.readUTF();
            ReentrantReadWriteLock fLock = getFileLocks(req_fileName);

            Lock readLock = fLock.readLock();
            //check write lock
            //write lock is already aquired
            if (fLock.isWriteLocked()) {
                data_out.writeUTF("FILE_ALREADY_LOCKED");
                System.out.println("Lock Denied!");
            } else {
                //aquire the lock
                readLock.lock();
                System.out.println("Locked!");
                heldLocks.put(this, fLock);
                data_out.writeUTF("FILE_LOCK_AVAILABLE");
            }
        }

        private void handleWriteLock() throws IOException {
            String req_fileName = data_in.readUTF();

            ReentrantReadWriteLock flock = getFileLocks(req_fileName);

            Lock readLock = flock.readLock();
            Lock writeLock = flock.writeLock();

            //either read or write lock is taken
            if (flock.isWriteLocked() || flock.getReadLockCount() != 0) {
                data_out.writeUTF("FILE_ALREADY_LOCKED");
            } else {
                writeLock.lock();
                heldLocks.put(this, flock);
                data_out.writeUTF("FILE_LOCK_AQUIRED");
            }
        }

        private void handleReadUnlockRequest() throws IOException {
            String req_fileName = data_in.readUTF();

            ReentrantReadWriteLock flLock = getFileLocks(req_fileName);

            Lock readLock = flLock.readLock();

            //unlock the lock
            readLock.unlock();
            data_out.writeUTF("FILE_READ_UNLOCKED");
            heldLocks.remove(this, flLock);
            System.out.println(req_fileName + " Unlocked!!");
        }

        private void handleNewFile() throws IOException {
            byte[] fb = new byte[data_in.readInt()];

            data_in.readFully(fb);
            publicFile pf = fd.deserializeObject(fb);

            fileList.add(pf);
            fileLocks.put(pf.getFileName(), new ReentrantReadWriteLock());

            fd.saveFile(pf);
            data_out.writeUTF("FILE_RECEIVED");
        }

        private void sendFileList() throws IOException {
            data_out.writeInt(fileList.size());

            for (publicFile fld : fileList) {
                byte[] srlF = fd.serializeObject(fld);
                //send the length
                data_out.writeInt(srlF.length);
                data_out.write(srlF);
            }
        }
        
        private void handleWriteUnlockRequest() throws IOException{
            String fileName = data_in.readUTF();
            
            ReentrantReadWriteLock writeLock = getFileLocks(fileName);
            Lock wlock = writeLock.writeLock();
            
            wlock.unlock();
            data_out.writeUTF("FILE_WRITE_UNLOCKED");
            heldLocks.remove(this, writeLock);
        }
        
        private void unlockAllFiles(){
            for (Map.Entry<clientThread, ReentrantReadWriteLock> entry : heldLocks.entrySet()) {
                ReentrantReadWriteLock value = entry.getValue();
                Lock rl = value.readLock();
                Lock wl = value.writeLock();
                
                if(value.isWriteLocked()){
                    wl.unlock();
                    System.out.println("Write Unlocked!");
                }
                if(value.getReadLockCount()!=0){
                    rl.unlock();
                    System.out.println("Read Unlocked!");
                }
            }
        }
        
        private void handleFileUpdateRequest() throws IOException{
            String filename = data_in.readUTF();
            int size = data_in.readInt();
            byte[] b=  new byte[size];
            data_in.readFully(b);
            
            publicFile pb = fd.deserializeObject(b);
            fd.saveFile(pb);
            fileList.clear();
            fileList = fd.getAllFiles();
            
            data_out.writeUTF("UPDATE_SUCCESS");
        }
    }
}
