package dfs_server;

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
