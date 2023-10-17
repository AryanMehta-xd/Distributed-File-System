package dfs_server;

import DAO.fileDAO;
import entities.publicFile;
import java.util.ArrayList;

/**
 *
 * @author Aryan Mehta
 */
public class DFS_server {

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
    
}
