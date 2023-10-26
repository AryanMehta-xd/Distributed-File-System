package dfs_server;

import DAO.fileDAO;


/**
 *
 * @author Aryan Mehta
 */
public class DFS_server {

    public static void main(String[] args) {
        ServerClientAuth clientAuth = new ServerClientAuth();
        Server server = new Server();
        clientAuth.start();
        server.start();
    }

}
